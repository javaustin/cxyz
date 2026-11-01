package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.config.CommandRestriction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class UUID implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        /*
        SYNTAX:
            /uuid [player]
            /uuid Steve
        */

        String node = "uuid";
        String node2 = "uuid.others";

        if (!sender.hasPermission("cxyz." + node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestriction.handleRestricted(node, sender)) {
            return true;
        }

        NetworkPlayer target;
        Map<String, Object> commonMap = new java.util.HashMap<>();

        if (args.length == 0 || !sender.hasPermission("cxyz." + node2)) {
            if (!(sender instanceof Player)) {
                // This would be a weird edge case, as it would require that a non-player (that also doesn't have permissions) sent this command with no args
                MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node2));
                return true;
            }

            target = NetworkPlayer.resolvePlayer(((Player) sender).getUniqueId());

            commonMap.putAll(MapFormatters.playerFormatter(target));
            commonMap.put("uuid", target.getUUID());

            MessageUtils.sendParsedMessage(sender, MessageKey.UUID, commonMap);
            return true;
        }

        target = NetworkPlayer.getPlayerByUsername(args[0]);

        if (target == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", args[0]));
            return true;
        }

        commonMap.putAll(MapFormatters.playerFormatter(target));
        commonMap.put("uuid", target.getUUID());

        MessageUtils.sendParsedMessage(sender, MessageKey.UUID, commonMap);

        return true;
    }

}
