package com.carrotguy69.cxyz.cmd.admin.rank;

import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.carrotguy69.cxyz.CXYZ.msgYML;
import static com.carrotguy69.cxyz.CXYZ.ranks;

public class RankList implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /rank list [player] [page]
            /rank [player] [page]
            /rank list Notch 2
            /rank list Notch
            /rank Notch
            /rank
        */

        String node = "cxyz.admin.rank.list";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestrictor.handleRestricted(command, sender)) {
            return true;
        }

        //        [-1]   [0]     [1]
        // "/rank list <player> [page]"



        boolean console = !(sender instanceof Player);

        Player p = null;

        if (!console) {
            p = (Player) sender;
        }

        NetworkPlayer np;
        if (args.length == 0 && console) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
            return true;
        }

        else if (args.length == 0)  // Player p is not null, and console is NOT the sender. The player is.
            np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());


        else
            np = NetworkPlayer.getPlayerByUsername(args[0]); // We ignore if the sender is a player or a console, we get the player by the username provided.

        int page = 1;
        try {
            if (args.length > 1) {
                page = Integer.parseInt(args[1]);
            }
        }
        catch (NumberFormatException ignored) {}


        if (np == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", args[0]));
            return true;
        }

        List<PlayerRank> playerRanks = np.getRanks().stream().sorted(Comparator.comparingInt(PlayerRank::getHierarchy).reversed()).collect(Collectors.toList());
        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);

        if (playerRanks.isEmpty()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.RANK_LIST_PLAYER_NONE, commonMap);
            return true;
        }

        String format = MessageGrabber.grab(MessageKey.RANK_LIST_PLAYER_FORMAT);
        String delimiter = MessageGrabber.grab(MessageKey.RANK_LIST_PLAYER_SEPARATOR);

        int maxEntriesPerPage = msgYML.getInt(MessageKey.RANK_LIST_PLAYER_MAX_ENTRIES.getPath(), -1);


        MapFormatters.ListFormatter formatter = MapFormatters.rankListFormatter(playerRanks, format, delimiter, maxEntriesPerPage, page);
        commonMap.putAll(formatter.getFormatMap());


        String unparsed = MessageGrabber.grab(MessageKey.RANK_LIST_PLAYER);

        int min = 1;
        int max = formatter.getMaxPages();

        if (page < 1 || page > max) {
            MessageUtils.sendParsedMessage(p, MessageKey.INVALID_PAGE, Map.of("min", min, "max", max, "page", page));
            return true;
        }

        unparsed = unparsed.replace("{ranks}", !formatter.getEntries().isEmpty() ? formatter.getText() : "None");

        commonMap.put("page", page);
        commonMap.put("previous-page", page - 1);
        commonMap.put("next-page", page + 1);
        commonMap.put("max-pages", max);

        MessageUtils.sendParsedMessage(sender, unparsed, commonMap);
        return true;

    }
}
