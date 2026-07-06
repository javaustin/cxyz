package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Smite implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.smite";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        NetworkPlayer np = NetworkPlayer.resolvePlayer(((Player) sender).getUniqueId());

        if (args.length >= 1 && sender.hasPermission(node + ".others")) {
            if (args[0].equalsIgnoreCase("*")) {
                smiteAll(sender);
                return true;
            }

            np = NetworkPlayer.getPlayerByUsername(args[0]);

            if (np == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", args[0]));
                return true;
            }

            if (np.getPlayer() == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_IS_OFFLINE, MapFormatters.playerFormatter(np));
                return true;
            }
        }

        smiteOne(sender, np);

        return true;
    }

    private static void smiteOne(CommandSender sender, NetworkPlayer target) {
        assert target.getPlayer().isOnline();

        target.getPlayer().getWorld().strikeLightning(target.getPlayer().getLocation());

        MessageUtils.sendParsedMessage(sender, MessageKey.SMITE, MapFormatters.playerFormatter(target));
    }

    private static void smiteAll(CommandSender sender) {

        int amount = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            amount++;

            p.getWorld().strikeLightning(p.getLocation());
        }

        MessageUtils.sendParsedMessage(sender, MessageKey.SMITE_ALL, Map.of("amount", amount));
    }


}
