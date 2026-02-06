package com.carrotguy69.cxyz.models.db;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.http.Request;
import com.carrotguy69.cxyz.models.config.cosmetics.ActiveCosmetic;
import com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic;
import com.carrotguy69.cxyz.models.config.GameServer;
import com.carrotguy69.cxyz.models.config.PlayerRank;
import com.carrotguy69.cxyz.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.cmd.admin.level._LevelExecutor;
import com.carrotguy69.cxyz.models.config.channel.utils.ChannelFunction;
import com.carrotguy69.cxyz.models.config.channel.utils.ChannelRegistry;
import com.carrotguy69.cxyz.other.*;

import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.utils.JsonConverters;
import com.carrotguy69.cxyz.other.utils.TimeUtils;
import com.google.common.graph.Network;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static com.carrotguy69.cxyz.CXYZ.*;
import static com.carrotguy69.cxyz.CXYZ.f;
import static com.carrotguy69.cxyz.models.config.PlayerRank.getRankByName;

public class NetworkPlayer {
    // To support GSON casting from String directly to this class, the variable types need to appear exactly as they are in the JSON string. (And by extension the database table itself)

    private String uuid;
    private String username;
    private String nickname;
    private String server;
    private String chat_tag;
    private String last_ip;
    private String timezone;
    private String message_privacy;
    private String friend_request_privacy;
    private String party_invite_privacy;
    private String chat_channel;
    private String chat_color;
    private String custom_rankplate;

    private int online;
    private int vanish;

    private long first_join;
    private long last_join;
    private long last_online;
    private long playtime;
    private long coins;
    private long xp;

    private int level;

    // String representations of lists
    private String ignore_list;
    private String friends;
    private String ranks;

    private String owned_cosmetics;
    private String equipped_cosmetics; // Equipped cosmetics does not represent what is physically equipped, it only represents what we should try to equip if the game allows us.

    private String muted_channels;

    public enum MessagePrivacy {
        ALLOWED,
        FRIENDS_ONLY,
        DISALLOWED
    }

    public enum FriendRequestPrivacy {
        ALLOWED,
        DISALLOWED
    }

    public enum PartyInvitePrivacy {
        ALLOWED,
        FRIENDS_ONLY,
        DISALLOWED
    }


    public NetworkPlayer createFromPlayer(Player p) {
        // Goal: Be able to create a representation of a network player to insert into the database.

        this.uuid = p.getUniqueId().toString();
        this.username = p.getName();
        this.nickname = "";
        this.ranks = "[\"" + defaultRank.getName() + "\"]";
        this.server = this_server.getName();
        this.online = p.isOnline() ? 1 : 0;
        this.first_join = TimeUtils.unixTimeNow();
        this.last_join = TimeUtils.unixTimeNow();
        this.last_online = TimeUtils.unixTimeNow();
        this.last_ip = p.getAddress() != null ? p.getAddress().getAddress().getHostAddress() : null;
        this.coins = 0;
        this.xp = 0;
        this.level = 0;
        this.playtime = 0; // We'll measure playtime in seconds. So we can easily convert with our Time helpers.
        this.timezone = CXYZ.timezone;
        this.vanish = 0;
        this.message_privacy = MessagePrivacy.ALLOWED.name();
        this.friend_request_privacy = FriendRequestPrivacy.ALLOWED.name();
        this.party_invite_privacy = PartyInvitePrivacy.ALLOWED.name();
        this.ignore_list = "[]";
        this.friends = "[]";
        this.owned_cosmetics = "[]";
        this.equipped_cosmetics = "[]";
        this.muted_channels = "[]";
        this.custom_rankplate = "";
        this.chat_channel = Objects.requireNonNull(ChannelRegistry.getChannelByFunction(ChannelFunction.PUBLIC)).getName(); // If the public channel is null then we have larger problems
        this.chat_tag = "";
        this.chat_color = "";

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof NetworkPlayer))
            return false;

        NetworkPlayer np = (NetworkPlayer) o;

        return
                Objects.equals(uuid, np.uuid)
                && Objects.equals(username, np.username);
    }

    @Override
    public String toString() {
        return "NetworkPlayer{" +
                "uuid=" + getUUID() +
                ", username='" + getUsername() + '\'' +
                ", nickname='" + getNickname() + '\'' +
                ", topRank=" + getTopRank().getName() +
                ", server=" + getServer().getName() +
                ", online=" + isOnline() +
                ", firstJoin=" + getFirstJoin() +
                ", lastJoin=" + getLastJoin() +
                ", lastOnline=" + getLastOnline() +
                ", lastIP='" + getLastIP() + '\'' +
                ", coins=" + getCoins() +
                ", xp=" + getXP() +
                ", level=" + getLevel() +
                ", playtime=" + getPlaytime() +
                ", timezone='" + getTimezone() + '\'' +
                ", vanished=" + isVanished() +
                ", messagePrivacy='" + getMessagePrivacy() + '\'' +
                ", friendPrivacy='" + getFriendPrivacy() + '\'' +
                ", partyPrivacy='" + getPartyPrivacy() + '\'' +
                ", ranks='" + (ranks != null ? ranks : "[]") +
                ", ignoreList=" + (ignore_list != null ? getIgnoreList().stream().map(NetworkPlayer::getUsername).collect(Collectors.toList()) : "[]") +
                ", friends=" + (friends != null ? getFriends().stream().map(NetworkPlayer::getUsername).collect(Collectors.toList())  : "[]") +
                ", ownedCosmetics=" + (owned_cosmetics != null ? getOwnedCosmetics().stream().map(Cosmetic::getId).collect(Collectors.toList())  : "[]") +
                ", equippedCosmetics=" + (equipped_cosmetics != null ? getEquippedCosmetics().stream().map(Cosmetic::getId).collect(Collectors.toList())  : "[]") +
                ", mutedChannels(size)=" + (muted_channels != null ? muted_channels  : "[]") +
                ", customRankPlate='" + getCustomRankPlate() + '\'' +
                ", chatChannel='" + getChatChannel().getName() + '\'' +
                ", chatTag='" + getChatTag() + '\'' +
                ", chatColor='" + getChatColor() + '\'' +
                '}';
    }

    public static NetworkPlayer getPlayerByUUID(UUID uuid) {

        NetworkPlayer np = users.get(uuid);

        if (np != null)
            return np;

        throw new RuntimeException("NetworkPlayer with uuid of " + uuid.toString() + " could not be found.");
    }

    public static NetworkPlayer getPlayerByUsername(String username) {
        // Accepts real username and display name.
        for (NetworkPlayer np : users.values()) {

            if (Objects.equals(np.getUsername().toLowerCase(), username.toLowerCase())) {
                return np;
            }

            if (Objects.equals(np.getDisplayName().toLowerCase(), username.toLowerCase())) {
                return np;
            }
        }

        return null;
    }

    public UUID getUUID() {
        return UUID.fromString(uuid);
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid.toString();
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDisplayName() {
        // Returns the nickname if it exists, username if not.
        if (nickname.isEmpty()) {
            return username;
        }

        return nickname;
    }

    public List<PlayerRank> getRanks() {
        List<PlayerRank> result = new ArrayList<>();

        List<String> list = JsonConverters.toList(this.ranks);

        for (String rankName : list) {
            try {
                PlayerRank rank = getRankByName(rankName);
                result.add(rank);
            }
            catch (Exception ex) {
                Logger.warning(String.format("Rank %s seems to no longer exist! It will remain attached to %s but will not be functional on this server.", rankName, this.getUsername()));
            }
        }

        return result.stream().sorted(
                        Comparator.comparingInt(PlayerRank::getHierarchy).reversed()
                )
                .collect(Collectors.toList());
    }

    public boolean hasRank(PlayerRank rank) {
        for (PlayerRank r : this.getRanks()) {
            if (r.getName().equals(rank.getName()))
                return true;
        }
        return false;
    }

    public PlayerRank getTopRank() {
        List<PlayerRank> list = getRanks().stream().sorted(
                Comparator.comparingInt(PlayerRank::getHierarchy).reversed()
                )
                .collect(Collectors.toList());

        return !list.isEmpty() ? list.getFirst() : defaultRank;
    }

    public void addRank(PlayerRank rank) {
        List<String> rankList = JsonConverters.toList(this.ranks);

        rankList.add(rank.getName());

        ranks = gson.toJson(rankList);
    }

    public void removeRank(PlayerRank rank) {
        List<String> rankList = JsonConverters.toList(this.ranks);

        rankList.remove(rank.getName());

        ranks = gson.toJson(rankList);
    }

    public GameServer getServer() {
        return GameServer.getServerFromName(server);
    }

    public void setServer(GameServer gameServer) {
        this.server = gameServer.getName();
    }

    public boolean isOnline() {
        return parseBoolean(online);
    }

    public void setOnline(boolean value) {
        this.online = value ? 1 : 0;
    }

    public long getFirstJoin() {
        return first_join;
    }

    public void setFirstJoin(long timestamp) {
        this.first_join = timestamp;
    }

    public long getLastJoin() {
        return last_join;
    }

    public void setLastJoin(long timestamp) {
        this.last_join = timestamp;
    }

    public long getLastOnline() {
        return last_online;
    }

    public void setLastOnline(long timestamp) {
        this.last_online = timestamp;
    }

    public String getLastIP() {
        return last_ip;
    }

    public void setLastIP(String ip) {
        this.last_ip = ip;
    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(long amount) {
        this.coins = amount;
    }

    public long getXP() {
        return xp;
    }

    public void setXP(long amount) {
        this.xp = amount;

        this.level = _LevelExecutor.xpToLevel(amount);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int i) {
        this.level = i;
    }

    public long getPlaytime() {
        return playtime;
    }

    public void setPlaytime(long playtime) {
        this.playtime = playtime;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public boolean isVanished() {
        return parseBoolean(vanish);
    }

    public boolean isVisibleTo(NetworkPlayer np) {
        // A NetworkPlayer should be able to see all NetworkPlayers with a lower rank hierarchy than them, no matter the vanish mode.
        if (this.isVanished() && this.getTopRank().getHierarchy() > np.getTopRank().getHierarchy()) {
            return false;
        }

        else return !this.isVanished();
    }

    public void setVanished(boolean value) {
        this.vanish = value ? 1 : 0;
    }

    public MessagePrivacy getMessagePrivacy() {
        return MessagePrivacy.valueOf(this.message_privacy);
    }

    public FriendRequestPrivacy getFriendPrivacy() {
        return FriendRequestPrivacy.valueOf(this.friend_request_privacy);
    }

    public PartyInvitePrivacy getPartyPrivacy() {
        return PartyInvitePrivacy.valueOf(this.party_invite_privacy);
    }

    public void setMessagePrivacy(MessagePrivacy value) {
        this.message_privacy = value.name();
    }
    public void setFriendPrivacy(FriendRequestPrivacy value) {
        this.friend_request_privacy = value.name();
    }
    public void setPartyPrivacy(PartyInvitePrivacy value) {
        this.party_invite_privacy = value.name();
    }

    public List<NetworkPlayer> getIgnoreList() {
        List<NetworkPlayer> result = new ArrayList<>();


        List<String> list = JsonConverters.toList(this.ignore_list);

        for (String playerUUID : list) {
            NetworkPlayer np = users.get(UUID.fromString(playerUUID));
            if (np != null)
                result.add(np);
        }

        return result;
    }

    public void addToIgnoreList(NetworkPlayer np) {
        String uuid = np.getUUID().toString();

        List<String> list = JsonConverters.toList(this.ignore_list);

        list.add(uuid);

        this.ignore_list = gson.toJson(list);
    }

    public void removeFromIgnoreList(NetworkPlayer np) {
        String uuid = np.getUUID().toString();

        List<String> list = JsonConverters.toList(this.ignore_list);

        list.remove(uuid);

        this.ignore_list = gson.toJson(list);
    }

    public boolean isIgnoring(NetworkPlayer np) {
        String uuid = np.getUUID().toString();

        List<String> list = JsonConverters.toList(this.ignore_list);

        return list.contains(uuid);
    }


    public List<String> getMutedChannels() {
        return JsonConverters.toList(this.muted_channels);
    }

    public List<String> getUnmutedChannels() {
        List<String> allChannels = BaseChannel.getChannelNames(false);

        allChannels.removeIf(this::isMutingChannel);

        return allChannels;
    }

    public void muteChannel(String channelName) {
        List<String> list = JsonConverters.toList(this.muted_channels);

        List<String> results = new ArrayList<>();

        for (String element : list) {
            results.add(element.toLowerCase());
        }

        results.add(channelName.toLowerCase());

        this.muted_channels = gson.toJson(results);
    }

    public void unmuteChannel(String channelName) {
        List<String> list = JsonConverters.toList(this.muted_channels);

        List<String> results = new ArrayList<>();

        for (String element : list) {
            results.add(element.toLowerCase());
        }

        results.remove(channelName.toLowerCase());

        this.muted_channels = gson.toJson(results);
    }

    public boolean isMutingChannel(String channelName) {
        List<String> list = JsonConverters.toList(this.muted_channels);

        List<String> results = new ArrayList<>();

        for (String element : list) {
            results.add(element.toLowerCase());
        }

        return results.contains(channelName.toLowerCase());
    }

    public boolean isMutingChannel(BaseChannel channel) {
        List<String> list = JsonConverters.toList(this.muted_channels);

        List<String> results = new ArrayList<>();

        for (String element : list) {
            results.add(element.toLowerCase());
        }

        return results.contains(channel.getName().toLowerCase());
    }

    public boolean canAccessChannel(BaseChannel channel) {
        return this.getPlayer().hasPermission("cxyz.chat." + channel.getName().toLowerCase());
    }

    public BaseChannel getChatChannel() {
        return BaseChannel.getChannel(chat_channel);
    }

    public void setChatChannel(BaseChannel channel) {
        this.chat_channel = channel.getName();
    }

    public void setChatChannel(String channel) {
        BaseChannel ch = BaseChannel.getChannel(channel);

        if (ch == null) {
            throw new IllegalArgumentException("Channel doesn't exist!");
        }

        this.chat_channel = channel;
    }



    public String getChatTag() {
        return chat_tag;
    }

    public void setChatTag(String chatTag) {
        this.chat_tag = chatTag;
    }




    public List<NetworkPlayer> getFriends() {
        List<NetworkPlayer> result = new ArrayList<>();


        List<String> list = JsonConverters.toList(this.friends);

        for (String uuid : list) {
            result.add(users.get(UUID.fromString(uuid)));
        }

        return result;
    }

    public void addFriend(NetworkPlayer np) {
        String uuid = np.getUUID().toString();

        List<String> list = JsonConverters.toList(this.friends);

        list.add(uuid);

        this.friends = gson.toJson(list);
    }

    public void removeFriend(NetworkPlayer np) {
        String uuid = np.getUUID().toString();

        List<String> list = JsonConverters.toList(this.friends);

        list.remove(uuid);

        this.friends = gson.toJson(list);
    }

    public boolean isFriendsWith(NetworkPlayer np) {
        String uuid = np.getUUID().toString();

        List<String> list = JsonConverters.toList(this.friends);

        return list.contains(uuid);
    }

    public List<Cosmetic> getOwnedCosmetics() {
        List<Cosmetic> result = new ArrayList<>();


        List<String> list = JsonConverters.toList(this.owned_cosmetics);

        for (String id : list) {

            Cosmetic cosmetic = Cosmetic.getCosmetic(id);

            if (cosmetic != null)
                result.add(cosmetic);
        }

        return result;
    }

    public void addOwnedCosmetic(Cosmetic cosmetic) {
        List<String> list = JsonConverters.toList(this.owned_cosmetics);

        list.add(cosmetic.getId());

        this.owned_cosmetics = gson.toJson(list);
    }

    public void removeOwnedCosmetic(Cosmetic cosmetic) {
        List<String> list = JsonConverters.toList(this.owned_cosmetics);

        list.remove(cosmetic.getId());

        this.owned_cosmetics = gson.toJson(list);
    }

    public boolean hasOwnedCosmetic(Cosmetic cosmetic) {
        List<String> list = JsonConverters.toList(this.owned_cosmetics);

        return list.contains(cosmetic.getId());
    }


    public List<Cosmetic> getEquippedCosmetics() {
        List<Cosmetic> result = new ArrayList<>();


        List<String> list = JsonConverters.toList(this.equipped_cosmetics);

        for (String id : list) {

            Cosmetic cosmetic = Cosmetic.getCosmetic(id);

            if (cosmetic != null)
                result.add(cosmetic);
        }

        return result;
    }

    public boolean hasEquippedCosmetic(Cosmetic cosmetic) {
        List<String> list = JsonConverters.toList(this.equipped_cosmetics);

        return list.contains(cosmetic.getId());
    }

    public void equipCosmetic(Cosmetic cosmetic) {
        // Functionally different from addEquippedCosmetic(); This function physically creates and runs the equipActions with the current NetworkPlayer.
        List<String> list = JsonConverters.toList(this.equipped_cosmetics);

        list.add(cosmetic.getId());

        this.equipped_cosmetics = gson.toJson(list);


        ActiveCosmetic ac = new ActiveCosmetic(cosmetic, this);
        ac.equip();

    }

    public void unEquipCosmeticOfType(Cosmetic.CosmeticType type) {
        for (Cosmetic cosmetic : getEquippedCosmetics()) {
            if (cosmetic.getType().equals(type)) {
                unEquipCosmetic(cosmetic);
            }
        }
    }

    public void unEquipCosmetic(Cosmetic cosmetic) {
        // For database consistency
        List<String> list = JsonConverters.toList(this.equipped_cosmetics);

        list.remove(cosmetic.getId());

        this.equipped_cosmetics = gson.toJson(list);

        List<ActiveCosmetic> activeCosmetics = ActiveCosmetic.activeCosmeticMap.get(this.getUUID());

        List<ActiveCosmetic> toRemove = new ArrayList<>();

        if (activeCosmetics != null) {
            for (ActiveCosmetic ac : activeCosmetics) {
                if (cosmetic.getId().equalsIgnoreCase(ac.getId())){
                    toRemove.add(ac);
                }
            }
        }

        for (ActiveCosmetic ac : toRemove) {
            ac.unEquip();
        }

    }

    public void unEquipActiveCosmetics() {


        List<ActiveCosmetic> activeCosmetics = ActiveCosmetic.activeCosmeticMap.get(this.getUUID());

        List<ActiveCosmetic> toRemove = new ArrayList<>();

        if (activeCosmetics != null) {
            toRemove.addAll(activeCosmetics);
        }

        for (ActiveCosmetic ac : toRemove) {
            ac.unEquip();
        }

        this.getEquippedCosmetics().clear();
    }


    public String getCustomRankPlate() {
        return custom_rankplate;
    }

    public void setCustomRankPlate(String rankPlate) {
        this.custom_rankplate = rankPlate;
    }

    public String getChatColor() {
        return chat_color;
    }

    public void setChatColor(String chatColor) {
        this.chat_color = chatColor;
    }

    public void sync() {
        Request.postRequest(apiEndpoint + "/user/modify", gson.toJson(this));
    }

    public void create() {
        Request.postRequest(apiEndpoint + "/user/create", gson.toJson(this));
    }

    public boolean isMuted() {
        return Punishment.getActivePunishment(this, Punishment.PunishmentType.MUTE) != null;
    }

    public boolean isBanned() {
        return Punishment.getActivePunishment(this, Punishment.PunishmentType.BAN) != null;
    }

    public boolean isInParty() {
        return Party.getPlayerParty(this.getUUID()) != null;
    }

    private static boolean parseBoolean(Number n) {
        return n != null && n.doubleValue() != 0.0;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(UUID.fromString(this.uuid));
    }

    public boolean sendUnparsedMessage(String content, Map<String, Object> formatMap) {
        if (!this.isOnline()) {
            return false;
        }

        if (Objects.equals(this.getServer().getName(), this_server.getName())) {
            // If the player is on our current server, we can just send them a message through Bukkit
            Player p = Bukkit.getPlayer(UUID.fromString(uuid));

            if (p == null) {
                return false;
            }

            MessageUtils.sendUnparsedMessage(this.getPlayer(), content, formatMap);
        }

        else {
            // If the player is NOT on our current server, we are going to send a request to the other server they ARE on with a message.
            Request.postRequest(Constants.getGameServerIP(this.getServer()) + "/sendMessage", gson.toJson(
                    Map.of(
                            "recipientUUID", uuid,
                            "content", String.join("\n", content),
                            "parsed", false,
                            "formatMap", formatMap
                    )
            ));

            
        }

        return true;
    }

    public boolean sendParsedMessage(String content, Map<String, Object> formatMap) {
        if (!this.isOnline()) {
            return false;
        }

        if (Objects.equals(this.getServer().getName(), this_server.getName())) {
            // If the player is on our current server, we can just send them a message through Bukkit
            Player p = Bukkit.getPlayer(UUID.fromString(uuid));

            if (p == null) {
                return false;
            }

            MessageUtils.sendParsedMessage(p, content, formatMap);
        }

        else {
            // If the player is NOT on our current server, we are going to send a request to the other server they ARE on with a message.
            Request.postRequest(Constants.getGameServerIP(this.getServer()) + "/sendMessage", gson.toJson(
                    Map.of(
                            "recipientUUID", uuid,
                            "content", String.join("\n", content),
                            "parsed", true,
                            "formatMap", formatMap
                    )
            ));

            
        }

        return true;
    }

    public void warp(GameServer destination) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("/send %s %s", this.getUsername(), destination.getName()));
    }

    public void kick(String reason) {
        // [!] There can be no cases where the players server mismatches the database value.

        if (!this.isOnline()) {
            return;
        }

        if (Objects.equals(this.getServer().getName(), CXYZ.this_server.getName())) {
            // If the player is on our current server, we can bypass the just kick them through Bukkit.
            Player p = Bukkit.getPlayer(UUID.fromString(uuid));

            kickPlayer(p, reason);
        }

        else {
            Request.postRequest(Constants.getGameServerIP(this.getServer()) + "/kickPlayer", gson.toJson(
                    Map.of(
                            "recipientUUID", this.getUUID().toString(),
                            "reason", reason
                    )
            ));
        }
    }


    // Static methods


    public static void kickPlayer(Player p, String reason) {
        if (p == null || !p.isOnline()) {
            return;
        }

        p.kickPlayer(f(reason));
    }




}
