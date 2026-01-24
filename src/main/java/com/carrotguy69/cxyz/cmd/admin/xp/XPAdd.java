package com.carrotguy69.cxyz.cmd.admin.xp;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class XPAdd implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /xp add <amount> [player]
            /xp add 50 Steve
        */

        // Surprisingly, this whole thing is a little tricky for a simple command.
        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.admin.xp.add";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        //               (-1)    0       1
        // Syntax: /xp add {amount} {player}

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
        amt = Math.abs(amt);

        long currentCoins = np.getXP();

        np.setCoins(currentCoins + amt);
        np.sync();

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);
        commonMap.put("amount", amt);

        MessageUtils.sendParsedMessage(sender, MessageKey.XP_ADDED, commonMap);

    }
}
