package com.carrotguy69.cxyz.cmd.mod;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Punishment;
import com.carrotguy69.cxyz.cmd.admin.Broadcast;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.other.*;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.utils.ObjectUtils;
import com.carrotguy69.cxyz.other.utils.TimeUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.*;
import static com.carrotguy69.cxyz.other.utils.TimeUtils.validTimeString;
import static com.carrotguy69.cxyz.messages.MessageParser.unescape;

public class Ban implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /ban <player> [duration] [reason] [-f | -s]
            /ban HackerSteve 24h hacking
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.mod.ban";
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

        String defaultReason = configYaml.getString("punishments.defaults.reasons.ban");

        if (args.length == 1) {
            // Only the player was provided - we still can execute with this
            String targetPlayer = args[0];

            long duration = configYaml.getLong("punishments.defaults.durations.effective-until.ban", -1);
            long effectiveUntilTimestamp = duration == -1 ? -1 : TimeUtils.unixTimeNow() + duration;

            ban(sender, targetPlayer, effectiveUntilTimestamp, defaultReason, silent, force);
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

            ban(sender, targetPlayer, effectiveUntilTimestamp, defaultReason, silent, force);
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

            ban(sender, targetPlayer, effectiveUntilTimestamp, String.join(" ", ObjectUtils.slice(args, 2, args.length)), silent, force);
        }

        return true;
    }

    private void ban(CommandSender sender, String targetPlayer, long effectiveUntilTimestamp, String reason, boolean silent, boolean force) {
        /*
        Simple 8-step process to ban a player!

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
            modUUID = "console";
            modUsername = "console";
        }
        else {
            Player modPlayer = (Player) sender;

            NetworkPlayer moderator = NetworkPlayer.getPlayerByUUID(modPlayer.getUniqueId());

            if (moderator.getTopRank().getHierarchy() <= player.getTopRank().getHierarchy()) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_OUTRANKS_SENDER, MapFormatters.playerSenderFormatter(player, moderator));
                return;
            }

            // This is just for logging purposes, when a player tries to talk or reconnect,
            // the server generates the message based off of the NetworkPlayer information it has right now.
            modUUID = moderator.getUUID().toString();
            modUsername = moderator.getUsername();
        }

        if (modUUID.equalsIgnoreCase(player.getUUID().toString())) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_IS_SELF, Map.of());
            return;
        }

        if (player.isBanned()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PUNISHMENT_ERROR_ALREADY_BANNED, MapFormatters.playerFormatter(player));
            return;
        }

        Punishment punishment = new Punishment();


        // We are 99% sure that our ID is correct. In the case that it isn't, it is corrected by the backend.
        // When a player joins again or a mod looks up the punishment after the original event, the message is generated with the updated value.
        long issuedTimestamp = TimeUtils.unixTimeNow();

        long expireDuration = (configYaml.getLong("punishments.defaults.durations.expire.ban", -1));

        punishment.setID(Punishment.generateID());

        punishment.setUUID(player.getUUID().toString());
        punishment.setUsername(player.getUsername());
        punishment.setModUUID(modUUID);
        punishment.setModUsername(modUsername);
        punishment.setType(String.valueOf(Punishment.PunishmentType.BAN));
        punishment.setEffectiveUntilTimestamp(effectiveUntilTimestamp);
        punishment.setExpireTimestamp(expireDuration == -1 ? -1 : issuedTimestamp + expireDuration);
        punishment.setIssuedTimestamp(issuedTimestamp);
        punishment.setReason(reason);
        punishment.setEnforced(true);

        punishment.create();


        punishmentIDMap.put(punishment.getID(), punishment);

        Map<String, Object> commonMap = MapFormatters.punishmentFormatter(sender, punishment);

        String playerMessage = MessageGrabber.grab(MessageKey.PUNISHMENT_BAN_PLAYER_MESSAGE, commonMap);
        String playerMessagePerm = MessageGrabber.grab(MessageKey.PUNISHMENT_BAN_PLAYER_MESSAGE_PERMANENT, commonMap);
        String modMessage = MessageGrabber.grab(MessageKey.PUNISHMENT_BAN_MOD_MESSAGE, commonMap);
        String logMessage = MessageGrabber.grab(MessageKey.PUNISHMENT_BAN_LOG_MESSAGE, commonMap);
        String announcement = MessageGrabber.grab(MessageKey.PUNISHMENT_BAN_ANNOUNCEMENT, commonMap);

        if (effectiveUntilTimestamp == -1)
            player.kick(f(String.join("\n", unescape(playerMessagePerm))));
        else
            player.kick(f(String.join("\n", unescape(playerMessage))));

        if (!modMessage.isEmpty()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PUNISHMENT_BAN_MOD_MESSAGE, commonMap);
        }

        if (!logMessage.isEmpty()) {
            Logger.punishment(MessageGrabber.grab(MessageKey.PUNISHMENT_BAN_LOG_MESSAGE), commonMap);
        }

        if (!silent && !announcement.isEmpty())
            Broadcast.broadcast(MessageGrabber.grab(MessageKey.PUNISHMENT_BAN_ANNOUNCEMENT), true, commonMap);
    }

        // deluxe menus

        // full ban gui:
        // - player directory (like a creative inventory with a search bar? cycle through players with that string name)
        // - duration menu - predefined durations (maybe drop and make reasons paired with durations)
        // - reason menu - predefined reasons

}
