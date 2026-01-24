package com.carrotguy69.cxyz.cmd.general.friend;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FriendList implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /friend list
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.friend.list";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        Player p = (Player) sender;

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        list(sender, np);

        return true;
    }

    public void list(CommandSender sender, NetworkPlayer np) {
        List<NetworkPlayer> players = np.getFriends();
        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);

        if (players.isEmpty()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.FRIEND_LIST_NONE, commonMap);
            return;
        }


        String format = MessageGrabber.grab(MessageKey.FRIEND_LIST_PLAYER_FORMAT);
        String delimiter = MessageGrabber.grab(MessageKey.FRIEND_LIST_PLAYER_SEPARATOR);

        MapFormatters.ListFormatter formatter = MapFormatters.playerListFormatter(players, format, delimiter);
        commonMap.putAll(formatter.getFormatMap());

        String unparsed = MessageGrabber.grab(MessageKey.FRIEND_LIST);

        unparsed = unparsed.replace("{friends}", !players.isEmpty() ? formatter.getText() : "None");
        unparsed = unparsed.replace("{players}", !players.isEmpty() ? formatter.getText() : "None");
        unparsed = unparsed.replace("{player-count}", String.valueOf(players.size()));

        MessageUtils.sendParsedMessage(sender, unparsed, commonMap);
        return;
    }

}
