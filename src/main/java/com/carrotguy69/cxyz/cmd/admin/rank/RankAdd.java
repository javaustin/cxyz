package com.carrotguy69.cxyz.cmd.admin.rank;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.config.PlayerRank;
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


public class RankAdd implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /rank add <rank> [player]
            /rank add admin Notch
        */

        String node = "cxyz.admin.rank.add";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestrictor.handleRestricted(command, sender)) {
            return true;
        }


        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "rank, player"));
            return true;
        }

        boolean console = !(sender instanceof Player);

        Player p = null;

        if (!console) {
            p = (Player) sender;
        }

        if (args.length == 1) {
            // If the player is not on console, try to apply the rank if they have permissions (already checked).

            // Let's parse the rank first, and then if the player is not present (the sender is from console) - then we will deny them.

            if (console) {
                MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
                return true;
            }

            else {
                NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

                addRank(sender, np, args[0]);
            }
        }

        if (args.length == 2) {
            // Apply the rank at `args[0] to the player at args[1]

            NetworkPlayer np = NetworkPlayer.getPlayerByUsername(args[1]);

            if (np == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", args[1]));
                return true;
            }

            addRank(sender, np, args[0]);

        }

        return true;

    }

    private static void addRank(CommandSender sender, NetworkPlayer np, String rankName) {
        try {
            PlayerRank rank = PlayerRank.getRankByName(rankName);

            Map<String, Object> commonMap = MapFormatters.playerFormatter(np);
            commonMap.putAll(MapFormatters.rankFormatter(rank));

            if (np.hasRank(rank)) {
                MessageUtils.sendParsedMessage(sender, MessageKey.RANK_ERROR_HAS_RANK, commonMap);
                return;
            }

            np.addRank(rank);
            np.sync();

            commonMap.putAll(MapFormatters.rankFormatter(rank));
            MessageUtils.sendParsedMessage(sender, MessageKey.RANK_ADD, commonMap);

        }
        catch (IllegalArgumentException ex) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_RANK, Map.of("input", rankName));
            return;
        }
    }
}
