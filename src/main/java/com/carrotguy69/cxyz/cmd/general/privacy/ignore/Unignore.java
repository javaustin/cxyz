package com.carrotguy69.cxyz.cmd.general.privacy.ignore;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Unignore implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        /*
        SYNTAX:
            /unignore <player>
            /unignore GoodSteve
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.unignore";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        Player p = (Player) sender;
        NetworkPlayer sp = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        if (args.length == 0) {
            IgnoreList.ignoreList(sender, sp, 1);
            return true;
        }

        NetworkPlayer tp = NetworkPlayer.getPlayerByUsername(args[0]);

        if (tp == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", args[0]));
            return true;
        }

        unignorePlayer(sp, tp);

        return true;
    }

    public void unignorePlayer(NetworkPlayer sender, NetworkPlayer target) {

        if (!sender.isIgnoring(target)) {
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.NOT_IGNORED_PLAYER, MapFormatters.playerSenderFormatter(target, sender));
            return;
        }

        sender.removeFromIgnoreList(target);
        sender.sync();

        MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.UNIGNORE_PLAYER, MapFormatters.playerSenderFormatter(target, sender));
    }
}
