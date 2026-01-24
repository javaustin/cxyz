package com.carrotguy69.cxyz.cmd.general.friend;

import com.carrotguy69.cxyz.models.db.FriendRequest;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.other.utils.TimeUtils;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static com.carrotguy69.cxyz.CXYZ.*;

public class FriendAdd implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /friend add <player>
            /friend add Notch
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.friend.add";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;


        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
            return true;
        }

        Player p = (Player) sender;

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        sendFriendRequest(np, args[0]);

        return true;
    }

    public void sendFriendRequest(NetworkPlayer sender, String recipientName) {

        NetworkPlayer recipient = NetworkPlayer.getPlayerByUsername(recipientName);

        if (recipient == null) {
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.PLAYER_NOT_FOUND, Map.of("username", recipientName));
            return;
        }

        Map<String, Object> commonMap = MapFormatters.playerSenderFormatter(recipient, sender);

        if (Objects.equals(sender.getUUID(), recipient.getUUID())) {
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.PLAYER_IS_SELF, Map.of());
            return;
        }

        if (sender.isFriendsWith(recipient)) {
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.FRIEND_ERROR_ALREADY_FRIENDS, commonMap); // already friends
            return;
        }

        if (!recipient.isOnline() || (recipient.isOnline() && !recipient.isVisibleTo(sender))) {
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.PLAYER_IS_OFFLINE, MapFormatters.playerFormatter(recipient));
            return;
        }

        if (FriendRequest.getLastFriendRequest(sender, recipient) != null) { // duplicate request "you already friend requested this player"
            commonMap.putAll(MapFormatters.playerFormatter(recipient));
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.FRIEND_ERROR_DUPLICATE_REQUEST, commonMap); // duplicate request "you already friend requested this player"
            return;
        }

        FriendRequest friendRequest = new FriendRequest(
                sender.getUUID().toString(),
                recipient.getUUID().toString(),
                TimeUtils.unixTimeNow() + friendRequestsExpireAfter
        );

        if (friendRequests.containsKey(sender.getUUID())) {
            // Do not allow duplicate requests
            Collection<FriendRequest> requests = friendRequests.get(sender.getUUID());

            for (FriendRequest request : requests) {
                if (Objects.equals(request.getRecipientUUID(), recipient.getUUID()) && request.getExpireTimestamp() > TimeUtils.unixTimeNow()) {
                    commonMap.putAll(MapFormatters.playerFormatter(recipient));
                    MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.FRIEND_ERROR_DUPLICATE_REQUEST, commonMap); // duplicate request (same as above)
                    return;
                }
            }
        }

        friendRequest.create();
        friendRequests.put(sender.getUUID(), friendRequest);

        recipient.sendParsedMessage(MessageGrabber.grab(MessageKey.FRIEND_REQUEST_RECEIVED), commonMap); // Sends the friend request message cross-server (if necessary.)

        MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.FRIEND_REQUEST_SENT, commonMap); // this sends to the sender
    }
}
