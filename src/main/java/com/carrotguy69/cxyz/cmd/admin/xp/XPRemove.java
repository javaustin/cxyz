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


public class XPRemove implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /xp remove <amount> [player]
            /xp remove 50 Steve
        */

        // Surprisingly, this whole thing is a little tricky for a simple command.
        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.admin.xp.remove";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        //                (-1)    0       1
        // Syntax: /xp remove {amount} {player}

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


            remove(sender, np, amt);
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

            remove(sender, np, amt);
        }

        return true;
    }

    public static void remove(CommandSender sender, NetworkPlayer np, long amt) {
        amt = Math.abs(amt);

        int currentXP = np.getLevel();

        if (currentXP - amt < 0)
            np.setXP(0);

        else
            np.setXP(currentXP - amt);

        np.sync();

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);
        commonMap.put("xp", amt);
        commonMap.put("amount", amt);

        MessageUtils.sendParsedMessage(sender, MessageKey.XP_REMOVED, commonMap);


    }
}
