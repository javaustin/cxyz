package com.carrotguy69.cxyz.cmd.mod;

import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.classes.models.db.Punishment;
import com.carrotguy69.cxyz.cmd.admin.Broadcast;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.template.MapFormatters;
import com.carrotguy69.cxyz.other.*;
import com.carrotguy69.cxyz.other.messages.MessageGrabber;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.*;
import static com.carrotguy69.cxyz.other.TimeUtils.validTimeString;

public class Mute implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        /*
        ex: "/ban player 1h20m hacking"
        args[0] is always the player
        args[1] is always the duration (can be "permanent")
        args[2] is always the reason (if present)
        ^ enforce these with tab completers

        - if not all args are present, we can make a cool inventory gui system
        - we need the player at the minimum
        - for other args

        flags:
        -s: silent, do not announce to chat
        -f: force, skip the gui menu
        */

        // we will code the base command first and then add gui support later
        // we don't to get stuck into the "infinitely detailed" trap

        String node = "cxyz.mod.mute";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        boolean silent = false;
        boolean force = false; // In the future we will use this to skip a GUI menu

        if (List.of(args).contains("-s")) {
            args = ObjectUtils.removeItem(args, "-s");
            silent = true;
        }

        if (List.of(args).contains("-f")) {
            args = ObjectUtils.removeItem(args, "-f");
            force = true;
        }

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player, duration, reason"));
            return true;
        }

        String defaultReason = configYaml.getString("punishments.defaults.reasons.mute");

        if (args.length == 1) {
            // Only the player was provided - we still can execute with this
            String targetPlayer = args[0];

            long duration = configYaml.getLong("punishments.defaults.durations.effective-until.mute", 1800);
            long effectiveUntilTimestamp = duration == -1 ? -1 : TimeUtils.unixTimeNow() + duration;

            mute(sender, targetPlayer, effectiveUntilTimestamp, defaultReason, silent, force);
            return true;
        }

        if (args.length == 2) {
            // Player and time are provided

            String targetPlayer = args[0];
            String timeString = args[1];

            if (!validTimeString(timeString) && !timeString.equals("permanent")) {
                MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_DURATION, Map.of("input", timeString));
                return true;
            }

            // If the timeString == "permanent" the duration is -1. We will check if the duration is -1 in the next line, and if it is we pass that as the effectiveUntilTimestamp.
            long duration = timeString.equalsIgnoreCase("permanent") ? -1 : TimeUtils.toSeconds(timeString);
            long effectiveUntilTimestamp = duration == -1 ? -1 : TimeUtils.unixTimeNow() + duration;

            mute(sender, targetPlayer, effectiveUntilTimestamp, defaultReason, silent, force);
            return true;
        }

        if (args.length >= 3) {
            // All necessary args are provided

            String targetPlayer = args[0];
            String timeString = args[1];

            if (!validTimeString(timeString) && !timeString.equals("permanent")) {
                MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_DURATION, Map.of("input", timeString));
                return true;
            }

            long duration = timeString.equalsIgnoreCase("permanent") ? -1 : TimeUtils.toSeconds(timeString);
            long effectiveUntilTimestamp = duration == -1 ? -1 : TimeUtils.unixTimeNow() + duration;

            mute(sender, targetPlayer, effectiveUntilTimestamp, String.join(" ", ObjectUtils.slice(args, 2, args.length)), silent, force);
        }

        return true;
    }

    private void mute(CommandSender sender, String targetPlayer, long effectiveUntilTimestamp, String reason, boolean silent, boolean force) {
        /*
        Simple 8-step process to mute a player!

        1. Initialize arguments we already have (duration, reason)
        2. Get user data
        3. Get moderator data
        4. Ensure moderator outranks user
        6. Create punishment object
        7. Kick player
        8. Log
        */

        NetworkPlayer player = NetworkPlayer.getPlayerByUsername(targetPlayer);

        if (player == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", targetPlayer));
            return;
        }

        String modUUID;
        String modUsername;

        if (!(sender instanceof Player)) {
            modUUID = "Console";
            modUsername = "Console";
        }
        else {
            Player modPlayer = (Player) sender;

            NetworkPlayer moderator = NetworkPlayer.getPlayerByUUID(modPlayer.getUniqueId());

            if (moderator.getRank().getHierarchy() <= player.getRank().getHierarchy()) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_OUTRANKS_SENDER, MapFormatters.playerSenderFormatter(player, moderator));
                return;
            }

            // This is just for logging purposes, when a player tries to talk or reconnect,
            // the server generates the message based off of the NetworkPlayer information it has right now.
            modUUID = moderator.getUUID().toString();
            modUsername = moderator.getUsername();
        }

        Punishment punishment = new Punishment();


        // We are 99% sure that our ID is correct. In the case that it isn't, it is corrected by the backend.
        // When a player joins again or a mod looks up the punishment after the original event, the message is generated with the updated value.
        punishment.setID(Punishment.generateID());

        punishment.setUUID(player.getUUID().toString());
        punishment.setUsername(player.getUsername());
        punishment.setModUUID(modUUID);
        punishment.setModUsername(modUsername);
        punishment.setType(String.valueOf(Punishment.PunishmentType.MUTE));
        punishment.setEffectiveUntilTimestamp(effectiveUntilTimestamp);
        punishment.setExpireTimestamp(-1);
        punishment.setIssuedTimestamp(TimeUtils.unixTimeNow());
        punishment.setReason(reason);
        punishment.setEnforced(true);

        punishment.create();

        punishmentIDMap.put(punishment.getID(), punishment);

        Map<String, Object> commonMap = MapFormatters.punishmentFormatter(sender, punishment);

        String playerMessage = MessageGrabber.grab(MessageKey.PUNISHMENT_MUTE_INITIAL_PLAYER_MESSAGE, commonMap);
        String modMessage = MessageGrabber.grab(MessageKey.PUNISHMENT_MUTE_MOD_MESSAGE, commonMap);
        String logMessage = MessageGrabber.grab(MessageKey.PUNISHMENT_MUTE_LOG_MESSAGE, commonMap);
        String announcement = MessageGrabber.grab(MessageKey.PUNISHMENT_MUTE_ANNOUNCEMENT, commonMap);


        if (!playerMessage.isEmpty() && player.isOnline()) {
            MessageUtils.sendParsedMessage(player.getPlayer(), MessageKey.PUNISHMENT_MUTE_INITIAL_PLAYER_MESSAGE, commonMap);
        }

        if (!modMessage.isEmpty()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PUNISHMENT_MUTE_MOD_MESSAGE, commonMap);
        }

        if (!logMessage.isEmpty())
            Logger.punishment(logMessage);

        if (!silent && !announcement.isEmpty())
            Broadcast.broadcast(announcement, true, commonMap);
    }


}
