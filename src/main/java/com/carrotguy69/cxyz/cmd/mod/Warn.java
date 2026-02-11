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

public class Warn implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /warn <player> [reason] [-f | -s]
            /warn StinkySteve stop being so smelly!
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.mod.warn";
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
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player, reason"));
            return true;
        }

        if (args.length == 1) {
            // Only the player was provided - we still can execute with this

            // update in the future if we see fit: we can have a config boolean that controls if a mod can provide no reason
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "reason"));
//            warn(sender, targetPlayer, defaultReason, silent, force);
            return true;
        }

        if (args.length >= 2) {
            // All necessary args are provided

            String targetPlayer = args[0];
            String reason = ObjectUtils.slice_(args, 1);

            warn(sender, targetPlayer, reason, silent, force);
        }

        return true;
    }

    private void warn(CommandSender sender, String targetPlayer, String reason, boolean silent, boolean force) {

        NetworkPlayer player = NetworkPlayer.getPlayerByUsername(targetPlayer);

        if (player == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", targetPlayer));
            return;
        }

        if (!player.isOnline() || !player.getPlayer().isOnline()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_IS_OFFLINE, MapFormatters.playerFormatter(player));
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

        Punishment punishment = new Punishment();


        // We are 99% sure that our ID is correct. In the case that it isn't, it is corrected by the backend.
        // When a player joins again or a mod looks up the punishment after the original event, the message is generated with the updated value.

        long issuedTimestamp = TimeUtils.unixTimeNow();

        long duration = (configYaml.getLong("punishments.defaults.durations.effective-until.warn", -1));
        long expireDuration = (configYaml.getLong("punishments.defaults.durations.expire.warn", -1));

        punishment.setID(Punishment.generateID());

        punishment.setUUID(player.getUUID().toString());
        punishment.setUsername(player.getUsername());
        punishment.setModUUID(modUUID);
        punishment.setModUsername(modUsername);
        punishment.setType(String.valueOf(Punishment.PunishmentType.WARN));
        punishment.setEffectiveUntilTimestamp(duration == -1 ? -1 : issuedTimestamp + duration);
        punishment.setExpireTimestamp(expireDuration == -1 ? -1 : issuedTimestamp + expireDuration);
        punishment.setIssuedTimestamp(issuedTimestamp);
        punishment.setReason(reason);
        punishment.setEnforced(true);

        punishment.create();

        punishmentIDMap.put(punishment.getID(), punishment);

        Map<String, Object> commonMap = MapFormatters.punishmentFormatter(sender, punishment);

        String playerMessage = MessageGrabber.grab(MessageKey.PUNISHMENT_WARN_PLAYER_MESSAGE, commonMap);
        String modMessage = MessageGrabber.grab(MessageKey.PUNISHMENT_WARN_MOD_MESSAGE, commonMap);
        String logMessage = MessageGrabber.grab(MessageKey.PUNISHMENT_WARN_LOG_MESSAGE, commonMap);
        String announcement = MessageGrabber.grab(MessageKey.PUNISHMENT_WARN_ANNOUNCEMENT, commonMap);


        if (!playerMessage.isEmpty()) {
            MessageUtils.sendParsedMessage(player.getPlayer(), MessageKey.PUNISHMENT_WARN_PLAYER_MESSAGE, commonMap);
        }
        
        if (!modMessage.isEmpty()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PUNISHMENT_WARN_MOD_MESSAGE, commonMap);
        }

        if (!logMessage.isEmpty())
            Logger.punishment(logMessage);

        if (!silent && !announcement.isEmpty())
            Broadcast.broadcast(announcement, true, commonMap);
    }


}
