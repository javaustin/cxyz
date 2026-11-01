package com.carrotguy69.cxyz.cmd.privacy;

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

public class FriendPrivacy implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        /*
        SYNTAX:
            /friendprivacy <ALLOWED | DISALLOWED>
            /friendprivacy ALLOWED
        */


        String node = "friendprivacy";

        if (!sender.hasPermission("cxyz." + node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestriction.handleRestricted(node, sender))
            return true;

        if (!(sender instanceof Player p)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }


        NetworkPlayer np = NetworkPlayer.resolvePlayer(p.getUniqueId());

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);

        if (args.length == 0) {
            commonMap.put("value", np.getFriendPrivacy().name());

            MessageUtils.sendParsedMessage(sender, MessageKey.FRIEND_PRIVACY_VIEW, commonMap);
            return true;
        }

        try {
            NetworkPlayer.FriendRequestPrivacy value = NetworkPlayer.FriendRequestPrivacy.valueOf(args[0].toUpperCase());

            np.setFriendPrivacy(value);
            np.sync();

            commonMap.put("value", np.getFriendPrivacy().name());

            MessageUtils.sendParsedMessage(sender, MessageKey.FRIEND_PRIVACY_SET, commonMap);


        }
        catch (IllegalArgumentException ex) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_FRIEND_PRIVACY_VALUE, Map.of("input", args[0]));
        }

        return true;
    }


}
