package com.carrotguy69.cxyz.cmd.mod;

import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.classes.models.db.Punishment;
import com.carrotguy69.cxyz.cmd.admin.Broadcast;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.template.MapFormatters;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.other.ObjectUtils;
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

import static com.carrotguy69.cxyz.CXYZ.punishmentIDMap;

public class Unban implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.mod.unban";
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

            unban(args[0], sender, silent);
        }
        else {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
        }

        return true;
    }

    private void unban(String target, CommandSender sender, boolean silent) {

        NetworkPlayer player = NetworkPlayer.getPlayerByUsername(target);


        if (player == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", target));
            return;
        }

        List<Punishment> punishments = Punishment.getActivePunishments(player, Punishment.PunishmentType.BAN);

        if (punishments.isEmpty()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PUNISHMENT_ERROR_NOT_BANNED, MapFormatters.playerFormatter(player));
            return;
        }

        boolean messageSent = false;
        // We only send our unban messages for the first punishment.
        // The rest of this system just ensures all other active bans are lifted so the unban isn't undermined.
        for (Punishment punishment : punishments) {

            if (!punishment.isEnforced())
                continue;

            punishment.setEnforced(false);

            if (sender instanceof Player) {
                Player editorModPlayer = (Player) sender;
                NetworkPlayer editorMod = NetworkPlayer.getPlayerByUUID(editorModPlayer.getUniqueId());
                punishment.setEditorMod(editorMod);
            }
            else {
                punishment.setEditorModUsername("Console");
                punishment.setEditorModUsername("Console");
            }

            punishment.edit(); // Syncs w/ API

            punishmentIDMap.put(punishment.getID(), punishment); // Redundancy is good I think?

            Map<String, Object> commonMap = MapFormatters.punishmentFormatter(sender, punishment);

            if (!messageSent) {
                String modMessage = MessageGrabber.grab(MessageKey.PUNISHMENT_UNBAN_MOD_MESSAGE, commonMap);
                String logMessage = MessageGrabber.grab(MessageKey.PUNISHMENT_UNBAN_LOG_MESSAGE, commonMap);
                String announcement = MessageGrabber.grab(MessageKey.PUNISHMENT_UNBAN_ANNOUNCEMENT, commonMap);


                if (!modMessage.isEmpty()) {
                    MessageUtils.sendParsedMessage(sender, MessageKey.PUNISHMENT_UNBAN_MOD_MESSAGE, commonMap);
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
