package com.carrotguy69.cxyz.cmd.general.friend;

import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.other.messages.MessageGrabber;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import com.carrotguy69.cxyz.template.MapFormatters;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.carrotguy69.cxyz.CXYZ.*;

public class List_ implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {


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

        list(np);

        return true;
    }

    public void list(NetworkPlayer np) {
        Player p = np.getPlayer();


        String format = MessageGrabber.grab(MessageKey.FRIEND_LIST_PLAYER_FORMAT);
        String delimiter = MessageGrabber.grab(MessageKey.FRIEND_LIST_PLAYER_SEPARATOR);

        List<String> formattedPlayers = new ArrayList<>();
        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);

        List<NetworkPlayer> players = np.getFriends();


        for (int i = 0; i < players.size(); i++) { // Create a string using the format template
            NetworkPlayer player = players.get(i);

            String playerString = format;

            playerString = playerString.replace("{player}", "{player-" + i + "}");
            playerString = playerString.replace("{player-nickname}", "{player-nickname-" + i + "}");
            playerString = playerString.replace("{player-display-name}", "{player-display-name-" + i + "}");
            playerString = playerString.replace("{player-uuid}", "{player-uuid-" + i + "}");
            playerString = playerString.replace("{player-rank}", "{player-rank-" + i + "}");
            playerString = playerString.replace("{player-prefix}", "{player-prefix-" + i + "}");
            playerString = playerString.replace("{player-rank-prefix-display}", "{player-rank-prefix-display-" + i + "}");
            playerString = playerString.replace("{player-color}", "{player-color-" + i + "}");
            playerString = playerString.replace("{player-chat-color}", "{player-chat-color-" + i + "}");
            playerString = playerString.replace("{player-tag}", "{player-tag-" + i + "}");

            playerString = playerString.replace("{player-server}", "{player-server-" + i + "}");
            playerString = playerString.replace("{player-level}", "{player-level-" + i + "}");
            playerString = playerString.replace("{player-xp}", "{player-xp-" + i + "}");
            playerString = playerString.replace("{player-online-status}", "{player-online-status-" + i + "}");
            playerString = playerString.replace("{player-ignored-channels}", "{player-ignored-channels-" + i + "}");

            formattedPlayers.add(playerString);

            commonMap.put("player-" + i, player.getUsername());
            commonMap.put("player-nickname-" + i, player.getNickname() != null ? player.getNickname() : "");
            commonMap.put("player-display-name-" + i, player.getDisplayName());
            commonMap.put("player-uuid-" + i, player.getUUID());
            commonMap.put("player-rank-" + i, player.getRank().getName());
            commonMap.put("player-prefix-" + i, player.getCustomRankPlate() != null && !player.getCustomRankPlate().isBlank() ? player.getCustomRankPlate() : player.getRank().getPrefix());
            commonMap.put("player-rank-prefix-display-" + i, ChatColor.stripColor(f(player.getRank().getPrefix())).isBlank() ? player.getRank().getColor() + player.getRank().getName().toUpperCase() : player.getRank().getPrefix());
            commonMap.put("player-color-" + i, player.getRank().getColor() != null ? player.getRank().getColor() : "&7");
            commonMap.put("player-chat-color-" + i, player.getChatColor() != null && !player.getChatColor().isBlank() ? player.getChatColor() : player.getRank().getDefaultChatColor());
            commonMap.put("player-tag-" + i, player.getChatTag() != null && !player.getChatTag().isBlank() ? player.getChatTag() : "");

            commonMap.put("player-server-" + i, player.getServer().getName());
            commonMap.put("player-level-" + i, player.getLevel());
            commonMap.put("player-xp-" + i, player.getXP());
            commonMap.put("player-online-status-" + i, player.isOnline() && !player.isVanished() ? "&aOnline" : "&7Offline");
            commonMap.put("player-ignored-channels-" + i, String.join(" ", player.getMutedChannels()));
        }

        // This returns the interactive formatting of the joined player list. It still needs to be parsed.
        String playerListString = String.join(delimiter, formattedPlayers);

        String unparsed = MessageGrabber.grab(MessageKey.FRIEND_LIST);

        unparsed = unparsed.replace("{friends}", playerListString);
        unparsed = unparsed.replace("{players}", playerListString);
        unparsed = unparsed.replace("{player-count}", String.valueOf(players.size()));

        MessageUtils.sendParsedMessage(p, unparsed, commonMap);
        return;
    }

}
