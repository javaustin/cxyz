package com.carrotguy69.cxyz.cmd.admin.coins;

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


public class CoinsView implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /coins view [player]
            /coins view Steve
            /coins
        */

        // Surprisingly, this whole thing is a little tricky for a simple command.

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.admin.coins.view";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        //                 0      1
        // Syntax: /coins view {player}

        else if (args.length == 0 && !(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
            return true;
        }

        else if (args.length == 0) {
            NetworkPlayer np = NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId());

            view(sender, np);

            return true;
        }

        else if (args.length == 1) {

            String nodeOthers = "cxyz.admin.coins.view.others";
            if (!sender.hasPermission(nodeOthers)) {
                view(sender, NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId()));
                return true;
            }

            // This is the only block of code that represents a sender entering a username

            String possibleUsername = args[0];

            NetworkPlayer np = NetworkPlayer.getPlayerByUsername(possibleUsername);

            if (np == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", possibleUsername));
                return true;
            }

            view(sender, np);
        }

        return true;
    }

    public static void view(CommandSender sender, NetworkPlayer np) {

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);
        commonMap.put("amount", np.getCoins());

        MessageUtils.sendParsedMessage(sender, MessageKey.COINS_VIEW, commonMap);
    }
}
