package com.carrotguy69.cxyz.cmd.general.friend;

import com.carrotguy69.cxyz.models.db.FriendRequest;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.utils.TimeUtils;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

import static com.carrotguy69.cxyz.CXYZ.friendRequests;

public class FriendDeny implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /friend deny <player>
            /friend deny LoserSteve
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.friend.deny";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("mising-args", "player"));
            return true;
        }

        Player p  = (Player) sender;

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        deny(np, args[0]);

        return true;
    }

    public static void deny(NetworkPlayer recipient, String senderName) {

        Player p = recipient.getPlayer();

        NetworkPlayer sender = NetworkPlayer.getPlayerByUsername(senderName);


        if (sender == null) {
            MessageUtils.sendParsedMessage(p, MessageKey.FRIEND_ERROR_NO_REQUEST, Map.of());
            return;
        }

        if (Objects.equals(sender.getUUID(), recipient.getUUID())) {
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.PLAYER_IS_SELF, Map.of());
            return;
        }

        FriendRequest request = FriendRequest.getLastFriendRequest(sender, recipient);

        if (request == null) {
            MessageUtils.sendParsedMessage(p, MessageKey.FRIEND_ERROR_NO_REQUEST, Map.of());
            return;
        }

        Map<String, Object> commonMap = MapFormatters.playerSenderFormatter(recipient, sender);

        if (recipient.isFriendsWith(sender)) {
            MessageUtils.sendParsedMessage(p, MessageKey.FRIEND_ERROR_ALREADY_FRIENDS, commonMap);
            return;
        }

        if (request.getExpireTimestamp() < TimeUtils.unixTimeNow()) {
            MessageUtils.sendParsedMessage(p, MessageKey.FRIEND_ERROR_REQUEST_EXPIRED, commonMap);
            return;
        }

        MessageUtils.sendParsedMessage(p, MessageKey.FRIEND_REQUEST_DENIED, commonMap);


        friendRequests.remove(request.getSenderUUID(), request);
        request.delete();
    }

}
