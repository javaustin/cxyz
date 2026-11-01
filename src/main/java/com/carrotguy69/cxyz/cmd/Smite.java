package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.config.CommandRestriction;
import com.carrotguy69.cxyz.utils.CommandUtils;
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
        /*
        SYNTAX:
            /smite [player]
            /smite Steve
            /smite *
            /smite
        */
        args = CommandUtils.handleSilent(args);

        String node = "smite";
        if (!sender.hasPermission("cxyz." + node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestriction.handleRestricted(node, sender))
            return true;

        Player p = null;

        if (args.length == 0 && !(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.MISSING_GENERAL), Map.of("missing-args", "player"));
            return true;
        }

        if (args.length == 0 || !sender.hasPermission("cxyz." + node + ".others")) {
            p = (Player) sender;
        }

        else {
            if (args[0].equalsIgnoreCase("*")) {
                smiteAll(sender);
                return true;
            }

            NetworkPlayer np = NetworkPlayer.getPlayerByUsername(args[0]);
            if (np != null)
                p = np.getPlayer();

            if ((p == null && np != null) || (np != null && !np.isVisibleTo(sender))) {
                MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.PLAYER_IS_OFFLINE), MapFormatters.playerFormatter(np));
                return true;
            }

            else if (np == null) {
                MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.PLAYER_NOT_FOUND), Map.of("username", args[0]));
                return true;
            }
        }

        NetworkPlayer np = NetworkPlayer.resolvePlayer(p.getUniqueId());

        smiteOne(sender, np);

        return true;
    }

    private static void smiteOne(CommandSender sender, NetworkPlayer target) {
        assert target.getPlayer() != null;

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
