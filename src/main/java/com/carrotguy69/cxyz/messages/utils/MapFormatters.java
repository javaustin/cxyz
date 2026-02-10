package com.carrotguy69.cxyz.messages.utils;

import com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic;
import com.carrotguy69.cxyz.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Party;
import com.carrotguy69.cxyz.models.config.PlayerRank;
import com.carrotguy69.cxyz.models.db.Punishment;

import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.other.utils.ObjectUtils;
import com.carrotguy69.cxyz.other.utils.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



import java.util.*;

import static com.carrotguy69.cxyz.CXYZ.*;
import static com.carrotguy69.cxyz.cmd.general.ChatColor.getColor;

public class MapFormatters {

    public static class ListFormatter {
        private final Map<String, Object> map;
        private final List<String> entries;
        private final String delimiter;
        private final int maxEntriesPerPage;
        private final int pageNumber;


        public ListFormatter(List<String> entries, String delimiter, Map<String, Object> map, int maxEntriesPerPage, int pageNumber) {
            this.entries = entries;
            this.delimiter = delimiter;
            this.map = map;
            this.maxEntriesPerPage = maxEntriesPerPage <= 0 ? 100 : maxEntriesPerPage;
            this.pageNumber = pageNumber;
        }

        public Map<String, Object> getFormatMap() {
            return map;
        }

        public String getDelimiter() {
            return this.delimiter;
        }

        public List<String> getEntries() {
            return this.entries;
        }

        public String getText() {
            return String.join(delimiter, entries);
        }

        public int getMaxPages() {
            return Math.max(1, (int) Math.ceil(entries.size() / (double) maxEntriesPerPage));
        }


        @Override
        public String toString() {
            return String.format("ListFormatter{map=%s, text=%s, maxEntriesPerPage=%d}", map, getText(), maxEntriesPerPage);
        }

        public String toCompactString() {
            return String.format("ListFormatter{map(size)=%d, text=%s, maxEntriesPerPage=%d}", map.size(), getText(), maxEntriesPerPage);
        }

        public String generatePage(int pageNumber) {
            /*

            [!] Using 1-based indexing instead of 0-based (page numbers start at 1 instead of 0)

            ex:
            let n = 21 (total entries)
            let m = 5 (max entries per page)
            let p = specified page number

            so:
            1 -> [0, 4]
            2 -> [5, 9]
            3 -> [10, 14]
            4 -> [15, 19]
            5 -> [20, 20] (1 entry leftover)

            for each page:
                start: (p - 1) * m
                end: min((p * m) - 1, n -1)

            available pages: ceil(double n / double m) -> ceil(21 / 5) -> ceil(4.1) -> 5
            full page available if: available pages > p
            half page available if: available pages == p
            no page if: available pages < p

            */

            int size = entries.size();

            int startIndex = (pageNumber - 1) * maxEntriesPerPage;
            int endIndex = Math.min((pageNumber * maxEntriesPerPage) - 1, size - 1);

            Logger.debugMessage("entries=" + entries);
            Logger.debugMessage("startIndex=" + startIndex);
            Logger.debugMessage("endIndex=" + endIndex);

            String result = String.join(delimiter, entries.subList(startIndex, endIndex + 1));

            Logger.debugMessage("subList=" + entries.subList(startIndex, endIndex + 1));
            Logger.debugMessage("result=" + result);

            return result;
        }

    }



    public static ListFormatter playerListFormatter(List<NetworkPlayer> players, String format, String delimiter, int maxEntriesPerPage, int pageNumber) {

        int size = players.size();

        int startIndex = (pageNumber - 1) * maxEntriesPerPage;
        int endIndex = Math.min((pageNumber * maxEntriesPerPage) - 1, size - 1);

        List<String> strings = new ArrayList<>(); // Each string contains the specified format with keys replaced with enumerated ones: "{player-color}{player}" -> "{player-color-0}{rank-0}"

        Map<String, Object> commonMap = new HashMap<>(); // Will represent all the placeholder keys and values we will fulfill at parse time.

        for (int i = startIndex; i < endIndex; i++) {
            String string = format; // Individual NetworkPlayer string
            NetworkPlayer np = players.get(i);

            for (Map.Entry<String, Object> entry : MapFormatters.playerFormatter(np).entrySet()) { // Add all keys and values from the single rank map formatter
                string = string.replace("{" + entry.getKey() + "}", "{" + entry.getKey() + "-" + i + "}"); // Enumerate placeholders in format string
                commonMap.put(entry.getKey() + "-" + i, entry.getValue()); // Add enumerated placeholders to commonMap.
            }

            strings.add(string);
        }


        return new ListFormatter(strings, delimiter, commonMap, maxEntriesPerPage, pageNumber);
    }

    public static ListFormatter rankListFormatter(List<PlayerRank> ranks, String format, String delimiter, int maxEntriesPerPage, int pageNumber) {

        int size = ranks.size();

        int startIndex = (pageNumber - 1) * maxEntriesPerPage;
        int endIndex = Math.min((pageNumber * maxEntriesPerPage) - 1, size - 1);

        List<String> strings = new ArrayList<>();

        Map<String, Object> commonMap = new HashMap<>();

        for (int i = startIndex; i < endIndex; i++) {
            String string = format;
            PlayerRank rank = ranks.get(i);

            for (Map.Entry<String, Object> entry : MapFormatters.rankFormatter(rank).entrySet()) {
                string = string.replace("{" + entry.getKey() + "}", "{" + entry.getKey() + "-" + i + "}");
                commonMap.put(entry.getKey() + "-" + i, entry.getValue());
            }

            strings.add(string);
        }


        return new ListFormatter(strings, delimiter, commonMap, maxEntriesPerPage, pageNumber);
    }

    public static ListFormatter punishmentListFormatter(CommandSender sender, List<Punishment> punishments, String format, String delimiter, int maxEntriesPerPage, int pageNumber) {

        int size = ranks.size();

        int startIndex = (pageNumber - 1) * maxEntriesPerPage;
        int endIndex = Math.min((pageNumber * maxEntriesPerPage) - 1, size - 1);

        List<String> strings = new ArrayList<>();

        Map<String, Object> commonMap = new HashMap<>();

        for (int i = startIndex; i < endIndex; i++) {
            String string = format;
            Punishment punishment = punishments.get(i);

            for (Map.Entry<String, Object> entry : MapFormatters.punishmentFormatter(sender, punishment).entrySet()) {
                string = string.replace("{" + entry.getKey() + "}", "{" + entry.getKey() + "-" + i + "}");
                commonMap.put(entry.getKey() + "-" + i, entry.getValue());
            }

            strings.add(string);
        }


        return new ListFormatter(strings, delimiter, commonMap, maxEntriesPerPage, pageNumber);
    }

    public static ListFormatter channelListFormatter(List<BaseChannel> channels, String format, String delimiter, int maxEntriesPerPage, int pageNumber) {

        int size = ranks.size();

        int startIndex = (pageNumber - 1) * maxEntriesPerPage;
        int endIndex = Math.min((pageNumber * maxEntriesPerPage) - 1, size - 1);

        List<String> strings = new ArrayList<>();

        Map<String, Object> commonMap = new HashMap<>();

        for (int i = startIndex; i < endIndex; i++) {
            String string = format;
            BaseChannel channel = channels.get(i);

            for (Map.Entry<String, Object> entry : MapFormatters.channelFormatter(channel).entrySet()) {
                string = string.replace("{" + entry.getKey() + "}", "{" + entry.getKey() + "-" + i + "}");
                commonMap.put(entry.getKey() + "-" + i, entry.getValue());
            }

            strings.add(string);
        }


        return new ListFormatter(strings, delimiter, commonMap, maxEntriesPerPage, pageNumber);
    }

    public static ListFormatter channelStringListFormatter(List<String> channels, String format, String delimiter, int maxEntriesPerPage, int pageNumber) {
        List<BaseChannel> chs = new ArrayList<>();

        for (String channelName : channels) {
            BaseChannel channel = BaseChannel.getChannel(channelName);

            if (channel != null) {
                chs.add(channel);
            }
        }

        return channelListFormatter(chs, format, delimiter, maxEntriesPerPage, pageNumber);
    }

    public static Map<String, Object> consoleFormatter() {
        // Basically a formatter that allows supports senderFormatter by attaching non-specific console values to player map keys.
        // So if a player uses {player-username} in the config, but the actual target is a console sender, this allows the key to still be parsed correctly regardless of the target,

        Map<String, Object> result = new HashMap<>();



        result.put("player", "console");
        result.put("player-uuid", "console");
        result.put("player-username", "console");
        result.put("player-nickname", "console");
        result.put("player-nickname-display", "console");
        result.put("player-display-name", "console");

        result.put("player-rank", defaultRank);
        result.put("player-rank-prefix", "");
        result.put("player-rank-prefix-display", "None");
        result.put("player-rank-color", defaultRank.getColor());

        result.put("player-custom-rankplate", "");
        result.put("player-custom-rankplate-display", "None");

        result.put("player-tag", "");
        result.put("player-tag-display", "None");

        result.put("player-chat-channel", "None");
        result.put("player-chat-channel-prefix", "None");

        result.put("player-chat-color", defaultRank.getColor());
        result.put("player-chat-color-name", com.carrotguy69.cxyz.cmd.general.ChatColor.getColorNameByCode(defaultRank.getColor()));

        result.put("player-server", this_server.getName());
        result.put("player-vanish-status", "&cUnvanished");
        result.put("player-online-status", "&aOnline");

        result.put("player-first-join", TimeUtils.dateOf(0, timezone));
        result.put("player-first-join-short", TimeUtils.dateOfShort(0, timezone));
        result.put("player-last-join", TimeUtils.dateOfShort(TimeUtils.unixTimeNow(), timezone));
        result.put("player-last-join-short", TimeUtils.dateOfShort(TimeUtils.unixTimeNow(), timezone));
        result.put("player-last-online", TimeUtils.dateOfShort(TimeUtils.unixTimeNow(), timezone));
        result.put("player-last-online-short", TimeUtils.dateOfShort(TimeUtils.unixTimeNow(), timezone));

        result.put("player-timezone", timezone);
        result.put("player-playtime", TimeUtils.countdownShort(0));
        result.put("player-last-ip", this_server.getIP());

        result.put("player-coins", 0);
        result.put("player-level", 0);
        result.put("player-xp", 0);

        result.put("player-friend-privacy", NetworkPlayer.FriendRequestPrivacy.ALLOWED.name());
        result.put("player-message-privacy", NetworkPlayer.MessagePrivacy.ALLOWED.name());
        result.put("player-party-privacy", NetworkPlayer.PartyInvitePrivacy.ALLOWED.name());

        result.put("player-ignore-list-size", 0);
        result.put("player-friend-list-size", 0);

        result.put("player-owned-cosmetics-list-size", 0);
        result.put("player-equipped-cosmetics-list-size", 0);

        result.put("player-muted-channels-list-size", 0);
        result.put("player-ignored-channels-list-size", 0);

        return result;
    }

    public static Map<String, Object> blankFormatter(String s) {
        // Reuse the consoleFormatter() map. We can replace the things where it says console with any string we want.
        // So in the event that there is a NoneType, we can account for it and format things to not applicable.

        Map<String, Object> original = consoleFormatter();
        Map<String, Object> temp = new HashMap<>();

        for (Map.Entry<String, Object> entry : original.entrySet()) {
            if (entry.getValue() instanceof String) {
                if (((String) entry.getValue()).contains("console")) {
                    String newVal = ((String) entry.getValue()).replace("console", s);

                    temp.put(entry.getKey(), newVal);
                }
            }

            else {
                temp.put(entry.getKey(), entry.getValue());
            }
        }

        return temp;
    }

    public static Map<String, Object> cloneFormaterToNewKey(Map<String, Object> originalMap, String fromKey, String toKey) {
        // Clones the playerFormatter so it can use player objets in different use cases.
        // e.g.: clonePlayerFormatter(playerFormatter(np), player, moderator) -> {player} will be {mod}

        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : originalMap.entrySet()) {
            String newKey = entry.getKey().replace(fromKey, toKey);
            Object value = entry.getValue();

            result.put(newKey, value);
        }

        return result;
    }



    public static Map<String, Object> playerFormatter(NetworkPlayer player) {

        Map<String, Object> commonMap = new HashMap<>();


        // For any value where it's possible to be null or blank, we will add a "-display" suffix that returns None if a null value exists.

        commonMap.put("player", player.getUsername());
        commonMap.put("player-uuid", player.getUUID());
        commonMap.put("player-username", player.getUsername());
        commonMap.put("player-nickname", player.getNickname() != null ? player.getNickname() : "");
        commonMap.put("player-nickname-display", player.getNickname() != null ? player.getNickname() : "None");
        commonMap.put("player-display-name", player.getDisplayName());

        commonMap.put("player-rank", player.getTopRank().getName());
        commonMap.put("player-rank-prefix", player.getCustomRankPlate() != null && !player.getCustomRankPlate().isBlank() ? player.getCustomRankPlate() : player.getTopRank().getPrefix());
        commonMap.put("player-rank-prefix-display", player.getCustomRankPlate() != null && !player.getCustomRankPlate().isBlank() ? player.getCustomRankPlate().strip() : (!player.getTopRank().getPrefix().isBlank()) ? player.getTopRank().getPrefix().strip() : player.getTopRank().getName().strip());
        commonMap.put("player-rank-color", player.getTopRank().getColor() != null ? player.getTopRank().getColor() : "&7");

        commonMap.put("player-custom-rankplate", player.getCustomRankPlate() != null && !player.getCustomRankPlate().isBlank() ? player.getCustomRankPlate() : "");
        commonMap.put("player-custom-rankplate-display", player.getCustomRankPlate() != null && !player.getCustomRankPlate().isBlank() ? player.getCustomRankPlate() : "");

        commonMap.put("player-tag", player.getChatTag() != null && !player.getChatTag().isBlank() ? player.getChatTag() : "");
        commonMap.put("player-tag-display", player.getChatTag() != null && !player.getChatTag().isBlank() ? player.getChatTag() : "None");

        commonMap.put("player-chat-channel", player.getChatChannel().getName());
        commonMap.put("player-chat-channel-prefix", player.getChatChannel().getPrefix());

        commonMap.put("player-chat-color", player.getChatColor() != null && !player.getChatColor().isBlank() ? player.getChatColor() : player.getTopRank().getDefaultChatColor());
        commonMap.put("player-chat-color-name", player.getChatColor() != null && !player.getChatColor().isBlank() ? com.carrotguy69.cxyz.cmd.general.ChatColor.getColorNameByCode(player.getChatColor()) : com.carrotguy69.cxyz.cmd.general.ChatColor.getColorNameByCode(player.getTopRank().getDefaultChatColor()));

        commonMap.put("player-server", player.getServer().getName());
        commonMap.put("player-vanish-status", player.isVanished() ? "&aVanished" : "&cUnvanished");
        commonMap.put("player-online-status", player.isOnline() && !player.isVanished() ? "&aOnline" : "&7Offline");

        commonMap.put("player-first-join", TimeUtils.dateOf(player.getFirstJoin(), player.getTimezone()));
        commonMap.put("player-first-join-short", TimeUtils.dateOfShort(player.getFirstJoin(), player.getTimezone()));
        commonMap.put("player-last-join", TimeUtils.dateOf(player.getLastJoin(), player.getTimezone()));
        commonMap.put("player-last-join-short", TimeUtils.dateOfShort(player.getLastOnline(), player.getTimezone()));
        commonMap.put("player-last-online", TimeUtils.dateOf(player.getLastOnline(), player.getTimezone()));
        commonMap.put("player-last-online-short", TimeUtils.dateOfShort(player.getLastOnline(), player.getTimezone()));

        commonMap.put("player-timezone", player.getTimezone());
        commonMap.put("player-playtime", TimeUtils.countdownShort(player.getPlaytime()));
        commonMap.put("player-last-ip", player.getLastIP());

        commonMap.put("player-coins", player.getCoins());
        commonMap.put("player-level", player.getLevel());
        commonMap.put("player-xp", player.getXP());

        commonMap.put("player-message-privacy", player.getMessagePrivacy().name());
        commonMap.put("player-friend-privacy", player.getFriendPrivacy().name());
        commonMap.put("player-party-privacy", player.getPartyPrivacy().name());

        commonMap.put("player-ignore-list-size", player.getIgnoreList().size());
        commonMap.put("player-friend-list-size", player.getFriends().size());

        commonMap.put("player-owned-cosmetics-list-size", player.getOwnedCosmetics().size());
        commonMap.put("player-equipped-cosmetics-list-size", player.getEquippedCosmetics().size());

        commonMap.put("player-muted-channels-list-size", player.getMutedChannels().size());
        commonMap.put("player-ignored-channels-list-size", player.getMutedChannels().size());


        return commonMap;
    }

    public static Map<String, Object> senderFormatter(CommandSender sender) {

        if (sender instanceof Player) {
            Player p = (Player) sender;
            NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

            return playerFormatter(np);
        }

        return consoleFormatter();
    }

    public static Map<String, Object> playerSenderFormatter(NetworkPlayer player, NetworkPlayer sender) {
        Map<String, Object> commonMap = playerFormatter(sender);
        commonMap.putAll(cloneFormaterToNewKey(commonMap, "player", "sender"));

        // Now the commonMap is full of all the sender keys, we now add the player key.
        commonMap.putAll(playerFormatter(player));

        return commonMap;
    }

    public static Map<String, Object> inviterRecipientFormat(NetworkPlayer inviter, NetworkPlayer recipient) {
        Map<String, Object> commonMap = new HashMap<>();

        commonMap.putAll(cloneFormaterToNewKey(playerFormatter(inviter), "player", "inviter"));
        commonMap.putAll(cloneFormaterToNewKey(playerFormatter(inviter), "player", "sender"));

        commonMap.putAll(cloneFormaterToNewKey(playerFormatter(recipient), "player", "recipient"));
        commonMap.putAll(cloneFormaterToNewKey(playerFormatter(recipient), "player", "player"));

        return commonMap;
    }

    public static Map<String, Object> punishmentFormatter(CommandSender sender, Punishment punishment) {
        // We added sender to allow timezones to be interpreted per user.

        NetworkPlayer punishedPlayer = NetworkPlayer.getPlayerByUUID(UUID.fromString(punishment.getUUID()));

        Map<String, Object> punishedPlayerMap = playerFormatter(punishedPlayer);

        Map<String, Object> commonMap = new HashMap<>(punishedPlayerMap);

        // Since it's possible for the moderators to be console, we must add consoleFormatters if the username is listed as "console".

        if (ObjectUtils.equalsIgnoreCaseNullSafe(punishment.getModUsername(), "console")) {
            commonMap.putAll(cloneFormaterToNewKey(consoleFormatter(), "player", "mod"));
        }

        else if (punishment.getModUsername() != null && !punishment.getModUsername().isBlank()) {
            NetworkPlayer mod = NetworkPlayer.getPlayerByUUID(UUID.fromString(punishment.getModUUID()));

            commonMap.putAll(cloneFormaterToNewKey(playerFormatter(mod), "player", "mod"));
        }


        // Editor mod logic (possibly null)

        if (punishment.getEditorModUsername() == null || punishment.getEditorModUsername().isBlank()) {
            commonMap.putAll(cloneFormaterToNewKey(blankFormatter("N/A"), "player", "editor-mod"));
        }

        else if (!ObjectUtils.equalsIgnoreCaseNullSafe(punishment.getEditorModUsername(), "console")) {
            NetworkPlayer editorMod = NetworkPlayer.getPlayerByUUID(UUID.fromString(punishment.getEditorModUUID()));

            commonMap.putAll(cloneFormaterToNewKey(playerFormatter(editorMod), "player", "editor-mod"));
        }

        else {
            commonMap.putAll(cloneFormaterToNewKey(consoleFormatter(), "player", "editor-mod"));
        }

        commonMap.put("case-id", punishment.getID());
        commonMap.put("type", punishment.getType().name());
        commonMap.put("type-lowercase", punishment.getType().name().toLowerCase());


        // We use the senders timezone instead of the requested players. This is why punished players should receive countdown messages.
        String tz = sender instanceof Player ? NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId()).getTimezone() : timezone;

        commonMap.put("date", TimeUtils.dateOf(punishment.getIssuedTimestamp(), tz));
        commonMap.put("date-short", TimeUtils.dateOfShort(punishment.getIssuedTimestamp(), tz));
        commonMap.put("effective-until", TimeUtils.dateOf(punishment.getEffectiveUntilTimestamp(), tz));
        commonMap.put("effective-until-short", TimeUtils.dateOfShort(punishment.getEffectiveUntilTimestamp(), tz));
        commonMap.put("effective-until-countdown", TimeUtils.unixCountdown(punishment.getEffectiveUntilTimestamp()));
        commonMap.put("effective-until-countdown-short", TimeUtils.unixCountdownShort(punishment.getEffectiveUntilTimestamp()));

        commonMap.put("expire-time", TimeUtils.dateOf(punishment.getExpireTimestamp(), tz));
        commonMap.put("expire-time-short", TimeUtils.dateOfShort(punishment.getExpireTimestamp(), tz));
        commonMap.put("expire-time-countdown", TimeUtils.unixCountdown(punishment.getExpireTimestamp()));
        commonMap.put("expire-time-countdown-short", TimeUtils.unixCountdownShort(punishment.getExpireTimestamp()));

        commonMap.put("enforced", String.valueOf(punishment.isEnforced()));
        commonMap.put("reason", punishment.getReason());


        return commonMap;
    }

    public static Map<String, Object> cosmeticFormatter(Cosmetic cosmetic) {
        Map<String, Object> commonMap = new HashMap<>();

        if (cosmetic == null) {
            throw new NullPointerException("cosmetic cannot be null!");
        }

        commonMap.put("id", cosmetic.getId());
        commonMap.put("lore", cosmetic.getLore());

        // See ActiveCosmetic::equip for CHAT_COLOR methodology/explanation

        if (cosmetic.getType().equals(Cosmetic.CosmeticType.CHAT_COLOR)) {
            String value = cosmetic.getDisplay().strip();

            com.carrotguy69.cxyz.cmd.general.ChatColor.Color color = getColor(value);

            if (color == null) {
                color = new com.carrotguy69.cxyz.cmd.general.ChatColor.Color("reset", "&r");
            }

            commonMap.put("display", color.code + color.name + " ");
        }

        else {
            commonMap.put("display", cosmetic.getDisplay());
        }

        commonMap.put("rank-requirement", cosmetic.getRequiredRank().getPrefix());
        commonMap.put("level-requirement", String.valueOf(cosmetic.getRequiredLevel()));
        commonMap.put("price", String.valueOf(cosmetic.getPrice()));
        commonMap.put("type", cosmetic.getType().name());

        return commonMap;
    }

    public static Map<String, Object> channelFormatter(BaseChannel channel) {

        Map<String, Object> commonMap = new HashMap<>();

        commonMap.put("channel-name", channel.getName());
        commonMap.put("channel-prefix", channel.getPrefix());
        commonMap.put("channel-chat-format", channel.getChatFormat());
        commonMap.put("channel-webhook-url", channel.getWebhookURL());
        commonMap.put("channel-trigger-prefix", channel.getTriggerPrefix());
        commonMap.put("channel-is-console-enabled", channel.isConsoleEnabled());
        commonMap.put("channel-is-ignorable", channel.isIgnorable());
        commonMap.put("channel-is-lockable", channel.isLockable());
        commonMap.put("channel-is-locked", channel.isLocked());
        commonMap.put("channel-is-read-only", channel.isReadOnly());

        return commonMap;
    }

    public static Map<String, Object> partyFormatter(Party party) {

        List<String> uuids = party.getPlayers() != null ? new ArrayList<>(party.getPlayers()) : new ArrayList<>();

        if (!uuids.contains(party.getOwnerUUID().toString())) { // Won't happen unless I refactor the API. (inverted)
            uuids.add(party.getOwnerUUID().toString());
        }

        NetworkPlayer owner = NetworkPlayer.getPlayerByUUID(party.getOwnerUUID());
        Map<String, Object> commonMap = cloneFormaterToNewKey(playerFormatter(owner), "player", "owner");

        commonMap.put("player-count", party.size() + 1);
        commonMap.put("max-player-count", partyMaxSize);
        // {players} is fulfilled in the code of the /party list command, not here.

        commonMap.put("type", party.isPublic() ? "Public" : "Private");

        return commonMap;
    }


    public static Map<String, Object> rankFormatter(PlayerRank rank) {
        Map<String, Object> commonMap = new HashMap<>();

        commonMap.put("rank-name", rank.getName());
        commonMap.put("rank-color", rank.getColor());
        commonMap.put("rank-prefix", rank.getPrefix()/*.isBlank() ? rank.getColor() + rank.getName(): rank.getPrefix()*/);
        commonMap.put("rank-prefix-display", ChatColor.stripColor(f(rank.getPrefix())).isBlank() ? rank.getColor() + rank.getName().toUpperCase().strip() : rank.getPrefix().strip());
        commonMap.put("rank-position", rank.getHierarchy());
        commonMap.put("rank-hierarchy", rank.getHierarchy());
        commonMap.put("rank-chat-color", rank.getDefaultChatColor());
        commonMap.put("rank-chat-cooldown", rank.getChatCooldown());

        return commonMap;
    }

}
