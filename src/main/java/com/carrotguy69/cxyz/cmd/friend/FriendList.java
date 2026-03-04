package com.carrotguy69.cxyz.cmd.friend;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.msgYML;

public class FriendList implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /friend list [page] [player]
        */

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.friend.list";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        NetworkPlayer np = null;
        int page = 1;


        if (args.length == 1) {
            try {
                page = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e) {
                MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_NUMBER, Map.of("input", args[0]));
                return true;
            }
        }

        if (args.length == 2 && sender.hasPermission(node + ".others")) {
            np = NetworkPlayer.getPlayerByUsername(args[1]);
            if (np == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", args[1]));
                return true;
            }
        }

        if (np == null) {
            if (sender instanceof Player) {
                np = NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId());
            }
            else {
                MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
                return true;
            }
        }


        list(sender, np, page);


        return true;
    }

    public void list(CommandSender sender, NetworkPlayer np, int page) {
        List<NetworkPlayer> players = np.getFriends();
        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);

        if (players.isEmpty()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.FRIEND_LIST_NONE, commonMap);
            return;
        }


        String format = MessageGrabber.grab(MessageKey.FRIEND_LIST_PLAYER_FORMAT);
        String delimiter = MessageGrabber.grab(MessageKey.FRIEND_LIST_PLAYER_SEPARATOR);


        int maxEntriesPerPage = msgYML.getInt(MessageKey.FRIEND_LIST_MAX_ENTRIES.getPath(), -1);

        MapFormatters.ListFormatter formatter = MapFormatters.playerListFormatter(players, format, delimiter, maxEntriesPerPage, page);
        commonMap.putAll(formatter.getFormatMap());

        String unparsed = MessageGrabber.grab(MessageKey.FRIEND_LIST);

        int min = 1;
        int max = formatter.getMaxPages();

        if (page < 1 || page > max) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_PAGE, Map.of("min", min, "max", max, "page", page));
            return;
        }

        unparsed = unparsed.replace("{friends}", !formatter.getEntries().isEmpty() ? formatter.generatePage(page) : "None");
        unparsed = unparsed.replace("{players}", !formatter.getEntries().isEmpty() ? formatter.generatePage(page) : "None");
        unparsed = unparsed.replace("{size}", String.valueOf(players.size()));

        commonMap.put("page", page);
        commonMap.put("previous-page", page - 1);
        commonMap.put("next-page", page + 1);
        commonMap.put("max-pages", max);

        MessageUtils.sendParsedMessage(sender, unparsed, commonMap);
    }

}
