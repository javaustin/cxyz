package com.carrotguy69.cxyz.template;

import com.carrotguy69.cxyz.classes.models.config.Cosmetic;
import com.carrotguy69.cxyz.classes.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.classes.models.db.Party;
import com.carrotguy69.cxyz.classes.models.config.PlayerRank;
import com.carrotguy69.cxyz.classes.models.db.Punishment;
import com.carrotguy69.cxyz.other.TimeUtils;
import com.carrotguy69.cxyz.other.messages.MessageGrabber;
import com.carrotguy69.cxyz.other.messages.MessageKey;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



import java.util.*;

import static com.carrotguy69.cxyz.CXYZ.*;

public class MapFormatters {


    public static Map<String, Object> consoleFormatter() {
        // Basically a formatter that allows supports senderFormatter by attaching non-specific console values to player map keys.
        // So if a player uses {player-username} in the config, but the actual target is a console sender, this allows the key to still be parsed correctly regardless of the target,

        Map<String, Object> result = new HashMap<>();

        result.put("player", "console");
        result.put("player-uuid", "console");
        result.put("player-username", "console");
        result.put("player-nickname", "console");
        result.put("player-display-name", "console");

        result.put("player-rank", defaultRank);
        result.put("player-prefix", defaultRank.getPrefix());
        result.put("player-rank-prefix-display", !defaultRank.getPrefix().isBlank() ? defaultRank.getPrefix() : "");

        result.put("player-custom-rankplate", !defaultRank.getPrefix().isBlank() ? defaultRank.getPrefix() : "");

        result.put("player-tag", "");
        result.put("player-chat-tag", "");

        result.put("player-chat-channel", "N/A");
        result.put("player-chat-channel-prefix", "N/A");

        result.put("player-color", defaultRank.getColor());
        result.put("player-chat-color", defaultRank.getColor());
        result.put("player-chat-color-name",
                com.carrotguy69.cxyz.cmd.general.ChatColor.getReverseMap().get(defaultRank.getColor()) != null
                        ? com.carrotguy69.cxyz.cmd.general.ChatColor.getReverseMap().get(defaultRank.getColor())
                        : defaultRank.getDefaultChatColor());

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
        result.put("player-playtime", TimeUtils.unixCountdownShort(0));
        result.put("player-last-ip", this_server.getIP());

        result.put("player-coins", 0);
        result.put("player-level", 0);
        result.put("player-xp", 0);

        result.put("player-friend-privacy", NetworkPlayer.FriendRequestPrivacy.ALLOWED);
        result.put("player-message-privacy", NetworkPlayer.MessagePrivacy.ALLOWED);
        result.put("player-party-privacy", NetworkPlayer.PartyInvitePrivacy.ALLOWED);

        result.put("player-ignore-list-size", 0);
        result.put("player-friend-list-size", 0);

        result.put("player-owned-cosmetics-list-size", 0);
        result.put("player-equipped-cosmetics-list-size", 0);

        result.put("player-muted-channel-list-size", 0);
        result.put("player-ignored-channel-list-size", 0);

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
        // e.g.: clonePlayerFormatter(playerFormatter(np), player, moderator) -> {player} will be {moderator}

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

        commonMap.put("player", player.getUsername());
        commonMap.put("player-uuid", player.getUUID());
        commonMap.put("player-username", player.getUsername());
        commonMap.put("player-nickname", player.getNickname() != null ? player.getNickname() : "");
        commonMap.put("player-display-name", player.getDisplayName());

        commonMap.put("player-rank", player.getRank().getName());
        commonMap.put("player-prefix", player.getCustomRankPlate() != null && !player.getCustomRankPlate().isBlank() ? player.getCustomRankPlate() : player.getRank().getPrefix());
        commonMap.put("player-rank-prefix-display", player.getCustomRankPlate() != null && !player.getCustomRankPlate().isBlank() ? player.getCustomRankPlate() : (!player.getRank().getPrefix().isBlank()) ? player.getRank().getPrefix() : player.getRank().getName());
        commonMap.put("player-custom-rankplate", player.getCustomRankPlate() != null && !player.getCustomRankPlate().isBlank() ? player.getCustomRankPlate() : "");

        commonMap.put("player-tag", player.getChatTag() != null && !player.getChatTag().isBlank() ? player.getChatTag() : "");
        commonMap.put("player-chat-tag", player.getChatTag() != null && !player.getChatTag().isBlank() ? player.getChatTag() : "");

        commonMap.put("player-chat-channel", player.getChatChannel().getName() != null && !player.getChatChannel().getName().isBlank() ? player.getChatChannel().getName() : "");
        commonMap.put("player-chat-channel-prefix", player.getChatChannel().getPrefix() != null && !player.getChatChannel().getPrefix().isBlank() ? player.getChatChannel().getPrefix() : "");

        commonMap.put("player-color", player.getRank().getColor() != null ? player.getRank().getColor() : "&7");
        commonMap.put("player-chat-color", player.getChatColor() != null && !player.getChatColor().isBlank() ? player.getChatColor() : player.getRank().getDefaultChatColor());
        commonMap.put("player-chat-color-name", player.getChatColor() != null && !player.getChatColor().isBlank() ? com.carrotguy69.cxyz.cmd.general.ChatColor.getColorByName(player.getChatColor()) : com.carrotguy69.cxyz.cmd.general.ChatColor.getColorByName(player.getRank().getDefaultChatColor()));

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
        commonMap.put("player-playtime", TimeUtils.unixCountdownShort(player.getPlaytime()));
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

        commonMap.put("player-muted-channel-list-size", player.getEquippedCosmetics().size());
        commonMap.put("player-ignored-channel-list-size", player.getEquippedCosmetics().size());


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

        if (punishment.getModUsername().equalsIgnoreCase("console")) {
            commonMap.putAll(cloneFormaterToNewKey(consoleFormatter(), "player", "mod"));
            commonMap.putAll(cloneFormaterToNewKey(consoleFormatter(), "player", "moderator"));
        }

        else if (punishment.getModUsername() != null && !punishment.getModUsername().isBlank()) {
            NetworkPlayer mod = NetworkPlayer.getPlayerByUUID(UUID.fromString(punishment.getModUUID()));

            commonMap.putAll(cloneFormaterToNewKey(playerFormatter(mod), "player", "mod"));
            commonMap.putAll(cloneFormaterToNewKey(playerFormatter(mod), "player", "moderator"));
        }

        if (punishment.getEditorModUsername() != null && !punishment.getEditorModUsername().isBlank()) {
            NetworkPlayer editorMod = NetworkPlayer.getPlayerByUUID(UUID.fromString(punishment.getModUUID()));

            commonMap.putAll(cloneFormaterToNewKey(playerFormatter(editorMod), "player", "mod"));
            commonMap.putAll(cloneFormaterToNewKey(playerFormatter(editorMod), "player", "moderator"));
        }

        else if (punishment.getEditorModUsername().equalsIgnoreCase("console")) {
            commonMap.putAll(cloneFormaterToNewKey(consoleFormatter(), "player", "editor-mod"));
            commonMap.putAll(cloneFormaterToNewKey(consoleFormatter(), "player", "editor-moderator"));
        }

        else if (punishment.getEditorModUsername().isBlank() || punishment.getEditorModUsername() == null) {
            commonMap.putAll(cloneFormaterToNewKey(blankFormatter("N/A"), "player", "editor-mod"));
            commonMap.putAll(cloneFormaterToNewKey(blankFormatter("N/A"), "player", "editor-moderator"));
        }

        commonMap.put("case-id", punishment.getID());
        commonMap.put("type", punishment.getType().name().toLowerCase());


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
        commonMap.put("display", cosmetic.getDisplay());
        commonMap.put("rank-requirement", cosmetic.getRankRequirement().getPrefix());
        commonMap.put("level-requirement", String.valueOf(cosmetic.getLevelRequirement()));
        commonMap.put("price", String.valueOf(cosmetic.getPrice()));

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
        // {players} is fulfilled in the code of the /party list command, not here.

        commonMap.put("type", party.isPublic() ? MessageGrabber.grab(MessageKey.PARTY_LIST_TYPE_PUBLIC) : MessageGrabber.grab(MessageKey.PARTY_LIST_TYPE_PRIVATE));

        return commonMap;
    }


    public static Map<String, Object> rankFormatter(PlayerRank rank) {
        Map<String, Object> commonMap = new HashMap<>();

        commonMap.put("rank-name", rank.getName());
        commonMap.put("rank-color", rank.getColor());
        commonMap.put("rank-prefix", rank.getPrefix()/*.isBlank() ? rank.getColor() + rank.getName(): rank.getPrefix()*/);
        commonMap.put("rank-prefix-display", ChatColor.stripColor(f(rank.getPrefix())).isBlank() ? rank.getColor() + rank.getName().toUpperCase() : rank.getPrefix());
        commonMap.put("rank-position", rank.getHierarchy());
        commonMap.put("rank-hierarchy", rank.getHierarchy());
        commonMap.put("rank-chat-color", rank.getDefaultChatColor());
        commonMap.put("rank-chat-cooldown", rank.getChatCooldown());

        return commonMap;
    }

}
