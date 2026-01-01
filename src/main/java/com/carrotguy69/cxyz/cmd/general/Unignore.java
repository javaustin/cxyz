package com.carrotguy69.cxyz.cmd.general;

import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.template.MapFormatters;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Unignore implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
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

        // I don't want to list people, we'll do it through the tab completer.

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
            return true;
        }

        NetworkPlayer tp = NetworkPlayer.getPlayerByUsername(args[0]);

        if (tp == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_PLAYER, Map.of("input", args[0]));
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
