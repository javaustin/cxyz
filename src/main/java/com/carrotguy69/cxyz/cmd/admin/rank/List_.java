package com.carrotguy69.cxyz.cmd.admin.rank;

import com.carrotguy69.cxyz.classes.models.config.PlayerRank;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.other.messages.MessageGrabber;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.carrotguy69.cxyz.CXYZ.f;
import static com.carrotguy69.cxyz.CXYZ.ranks;

public class List_ implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        String node = "cxyz.admin.rank.list";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestrictor.handleRestricted(command, sender)) {
            return true;
        }


        String format = MessageGrabber.grab(MessageKey.RANK_RANK_FORMAT);
        String delimiter = MessageGrabber.grab(MessageKey.RANK_RANK_SEPARATOR);

        List<String> formattedRanks = new ArrayList<>();
        Map<String, Object> commonMap = new HashMap<>();

        for (int i = 0; i < ranks.size(); i++) { // Create a string using the rank format template. We will replace the
            PlayerRank rank = ranks.get(i);

            String rankString = format;

            rankString = rankString.replace("{rank-name}", "{rank-name-" + i + "}");
            rankString = rankString.replace("{rank-color}", "{rank-color-" + i + "}");
            rankString = rankString.replace("{rank-prefix}", "{rank-prefix-" + i + "}");
            rankString = rankString.replace("{rank-prefix-display}", "{rank-prefix-display-" + i + "}");
            rankString = rankString.replace("{rank-position}", "{rank-position-" + i + "}");
            rankString = rankString.replace("{rank-hierarchy}", "{rank-hierarchy-" + i + "}");
            rankString = rankString.replace("{rank-chat-color}", "{rank-chat-color-" + i + "}");
            rankString = rankString.replace("{rank-chat-cooldown}", "{rank-chat-cooldown-" + i + "}");

            formattedRanks.add(rankString);

            commonMap.put("rank-name-" + i, rank.getName());
            commonMap.put("rank-color-" + i, rank.getColor());
            commonMap.put("rank-prefix-" + i, rank.getPrefix()/*.isBlank() ? rank.getColor() + rank.getName(): rank.getPrefix()*/);
            commonMap.put("rank-prefix-display-" + i, ChatColor.stripColor(f(rank.getPrefix())).isBlank() ? rank.getColor() + rank.getName().toUpperCase() : rank.getPrefix());
            commonMap.put("rank-position-" + i, String.valueOf(rank.getHierarchy()));
            commonMap.put("rank-hierarchy-" + i, String.valueOf(rank.getHierarchy()));
            commonMap.put("rank-chat-color-" + i, rank.getDefaultChatColor());
            commonMap.put("rank-chat-cooldown-" + i, rank.getChatCooldown());
        }

        String rankListString = String.join(delimiter, formattedRanks); // This returns the interactive formatting of the joined rank list.

        String unparsed = MessageGrabber.grab(MessageKey.RANK_LIST);

        unparsed = unparsed.replace("{ranks}", !ranks.isEmpty() ? rankListString : "None");

        MessageUtils.sendParsedMessage(sender, unparsed, commonMap);
        return true;
    }
}
