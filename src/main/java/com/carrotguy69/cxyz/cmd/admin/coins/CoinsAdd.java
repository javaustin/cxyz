package com.carrotguy69.cxyz.cmd.admin.coins;

import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.template.MapFormatters;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CoinsAdd implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        // Surprisingly, this whole thing is a little tricky for a simple command.

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.admin.coins.add";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        //               (-1)    0       1
        // Syntax: /coins add {amount} {player}

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "amount, player"));
            return true;
        }

        else if (args.length == 1 && !(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
            return true;
        }

        else if (args.length == 1) {
            // Player is themselves

            Player p = (Player) sender;

            long amt;

            try {
                amt = Long.parseLong(args[0]);
            }
            catch (IllegalArgumentException ex) {
                MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_NUMBER, Map.of("input", args[0]));
                return true;
            }

            NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

            add(sender, np, amt);
            return true;
        }

        else if (args.length == 2) {
            // This is the only block of code that represents a sender entering a username

            long amt;

            try {
                amt = Long.parseLong(args[0]);
            }
            catch (IllegalArgumentException ex) {
                MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_NUMBER, Map.of("input", args[0]));
                return true;
            }


            // Find player (possibly offline - need database)
            String possibleUsername = args[1];

            NetworkPlayer np = NetworkPlayer.getPlayerByUsername(possibleUsername);

            if (np == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", args[1]));
                return true;
            }

            add(sender, np, amt);
        }

        return true;
    }

    public static void add(CommandSender sender, NetworkPlayer np, long amt) {
        long absoluteAmount = Math.abs(amt);

        np.addCoins(amt);

        np.sync();

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);
        commonMap.put("coins", absoluteAmount);
        commonMap.put("amount", absoluteAmount);

        MessageUtils.sendParsedMessage(sender, MessageKey.COINS_ADDED, commonMap);

    }
}
