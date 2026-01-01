package com.carrotguy69.cxyz.cmd.admin.rank;

import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.classes.models.config.PlayerRank;
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

import static com.carrotguy69.cxyz.other.messages.MessageKey.RANK_VIEW;

public class View implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        String node = "cxyz.admin.rank.view";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestrictor.handleRestricted(command, sender)) {
            return true;
        }

        //        [-1]     [0]
        // "/rank view  <player>



        boolean console = !(sender instanceof Player);

        Player p = null;

        if (!console) {
            p = (Player) sender;
        }

        if (args.length == 0 && console) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
            return true;
        }

        else if (args.length == 0 && p != null) { // Player p is not null, and console is NOT the sender. The player is.
            NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

            PlayerRank rank = np.getRank();


            Map<String, Object> commonMap = MapFormatters.playerFormatter(np);
            commonMap.putAll(MapFormatters.rankFormatter(rank));

            MessageUtils.sendParsedMessage(p, MessageKey.RANK_VIEW, commonMap);
            return true;
        }

        if (args.length >= 1) {
            // We ignore if the sender is a player or a console, we get the player by the username provided.

            NetworkPlayer np = NetworkPlayer.getPlayerByUsername(args[0]);

            if (np == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", args[0]));
                return true;
            }

            PlayerRank rank = np.getRank();


            Map<String, Object> commonMap = MapFormatters.playerFormatter(np);
            commonMap.putAll(MapFormatters.rankFormatter(rank));

            MessageUtils.sendParsedMessage(sender, RANK_VIEW, commonMap);
            return true;

        }

        return true;

    }
}
