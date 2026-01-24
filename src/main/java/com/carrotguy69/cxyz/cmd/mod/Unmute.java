package com.carrotguy69.cxyz.cmd.mod;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Punishment;
import com.carrotguy69.cxyz.cmd.admin.Broadcast;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.other.utils.ObjectUtils;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.f;
import static com.carrotguy69.cxyz.CXYZ.punishmentIDMap;

public class Unmute implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /unmute <player> [-f | -s]
            /unmute GoodSteve
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.mod.unmute";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        boolean silent = false;

        if (args.length != 0) {
            if (List.of(args).contains("-s")) {
                args = ObjectUtils.removeItem(args, "-s");
                silent = true;
            }

            unmute(args[0], sender, silent);
        }
        else {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
        }

        return true;
    }

    private void unmute(String target, CommandSender sender, boolean silent) {

        NetworkPlayer player = NetworkPlayer.getPlayerByUsername(target);

        if (player == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", target));
            return;
        }

        List<Punishment> punishments = Punishment.getActivePunishments(player, Punishment.PunishmentType.MUTE);

        if (punishments.isEmpty()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PUNISHMENT_ERROR_NOT_MUTED, MapFormatters.playerFormatter(player));
            return;
        }

        boolean messageSent = false;
        // We only send our unmute messages for the first punishment.
        // The rest of this system just ensures all other active bans are lifted so the unmute isn't undermined.
        for (Punishment punishment : punishments) {

            punishment.setEnforced(false);

            if (sender instanceof Player) {
                Player editorModPlayer = (Player) sender;
                NetworkPlayer editorMod = NetworkPlayer.getPlayerByUUID(editorModPlayer.getUniqueId());
                punishment.setEditorMod(editorMod);
            }
            else {
                punishment.setEditorModUUID("Console");
                punishment.setEditorModUsername("Console");
            }

            punishment.edit(); // Syncs w/ API

            punishmentIDMap.put(punishment.getID(), punishment); // Unnecessary probably, since the punishment reference is already being edited. But redundancy is good I think?

            Map<String, Object> commonMap = MapFormatters.punishmentFormatter(sender, punishment);

            if (!messageSent) {
                String modMessage = MessageGrabber.grab(MessageKey.PUNISHMENT_UNMUTE_MOD_MESSAGE, commonMap);
                String logMessage = MessageGrabber.grab(MessageKey.PUNISHMENT_UNMUTE_LOG_MESSAGE, commonMap);
                String announcement = MessageGrabber.grab(MessageKey.PUNISHMENT_UNMUTE_ANNOUNCEMENT, commonMap);


                if (!modMessage.isEmpty()) {
                    MessageUtils.sendParsedMessage(sender, MessageKey.PUNISHMENT_UNMUTE_MOD_MESSAGE, commonMap);
                }

                if (!logMessage.isEmpty())
                    Logger.punishment(logMessage);

                if (!silent && !announcement.isEmpty())
                    Broadcast.broadcast(announcement, true, commonMap);

                messageSent = true;
            }
        }


    }
}
