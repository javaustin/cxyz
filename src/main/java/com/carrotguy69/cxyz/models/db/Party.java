package com.carrotguy69.cxyz.models.db;

import com.carrotguy69.cxyz.http.Request;
import com.carrotguy69.cxyz.models.config.channel.coreChannels.PartyChannel;
import com.carrotguy69.cxyz.models.config.channel.utils.ChannelRegistry;
import com.carrotguy69.cxyz.models.config.channel.utils.ChannelFunction;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.other.utils.JsonConverters;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.google.gson.annotations.SerializedName;

import java.util.*;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Party {
    // on party transfer ownership, make sure to delete the initial row with the original ownerUUID.

    // If public: Any player can run /p join <player> and they will join the party. Private invites can still be sent and will be processed the same.
    // If private: A player has to be invited to join the party, and they will join using /p join.
    // So the only logic the public boolean will affect is the join command.

    private String ownerUUID;
    private String players;

    @SerializedName("public")
    private int public_;

    public Party(String ownerUUID, String players, int public_) {
        this.ownerUUID = ownerUUID;
        this.players = players;
        this.public_ = public_;
    }

    public boolean isPublic() {
        return public_ == 1;
    }

    public List<String> getPlayers() {
        return JsonConverters.toList(players);
    }

    public int size() {
        return getPlayers().size();
    }

    public UUID getOwnerUUID() {
        return UUID.fromString(ownerUUID);
    }

    public void setOwnerUUID(UUID uuid) {
        this.ownerUUID = uuid.toString();
    }

    public void addPlayer(UUID uuid) {

        if (getPlayers().contains(uuid.toString())) {
            Logger.warning("Duplicate player in party! Removing...");
            removePlayer(uuid); // Remove any duplicates
        }

        List<String> _players_ = getPlayers();
        _players_.add(uuid.toString());

        players = gson.toJson(_players_).replace("\"", "'");
    }

    public void removePlayer(UUID uuid) {
        List<String> _players_ = getPlayers();
        _players_.remove(uuid.toString());

        players = gson.toJson(_players_).replace("\"", "'");
    }

    public void setPublic(boolean value) {
        this.public_ = value ? 1 : 0;
    }

    @Override
    public String toString() {
        return String.format("Party{ownerUUID=%s, players=%s, public=%b}", ownerUUID, players, isPublic());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Party))
            return false;

        Party party = (Party) o;

        return
                Objects.equals(ownerUUID, party.ownerUUID)
                && Objects.equals(players, party.players)
                && public_ == party.public_;
    }

    public static Party getPlayerParty(UUID uuid) {
        if (parties.get(uuid) != null) {
            return parties.get(uuid);
        }

        for (Party party : parties.values()) {
            if (party.getPlayers() != null && party.getPlayers().contains(uuid.toString())) {
                return party;
            }
        }

        return null;
    }

    public static boolean isPartyOwner(UUID uuid) {
        return parties.get(uuid) != null;
    }

    public static boolean isPartyMember(UUID uuid) {
        for (Party party : parties.values()) {
            if (party.getPlayers() != null && party.getPlayers().contains(uuid.toString())) {
                return true;
            }
        }

        return false;
    }

    public void sync() {
        // For party transferring: do not change the owner UUID and sync. The database will not know which value to update. Delete the party and create a new one.
        Request.postRequest(api_endpoint + "/party/sync", gson.toJson(Map.of("sender_uuid", this.ownerUUID, "players", this.players, "public", this.public_)));

        
    }

    public void create() {
        Request.postRequest(api_endpoint + "/party/create", gson.toJson(Map.of("sender_uuid", this.ownerUUID, "players", this.players, "public", this.public_)));
    }

    public void delete() {
        parties.remove(UUID.fromString(this.ownerUUID), this);
        partyInvites.removeAll(UUID.fromString(this.ownerUUID));

        Request.postRequest(api_endpoint + "/party/delete", gson.toJson(Map.of("sender_uuid", this.ownerUUID)));

        
    }



    public void announce(String text, Map<String, Object> formatMap) {
        // This will always use parsed messages, because there is no player input in this function

        List<NetworkPlayer> partyMembers = new ArrayList<>();

        for (String uuid : this.getPlayers()) {
            NetworkPlayer np = NetworkPlayer.getPlayerByUUID(UUID.fromString(uuid));
            partyMembers.add(np);
        }

        partyMembers.add(NetworkPlayer.getPlayerByUUID(UUID.fromString(ownerUUID)));


        for (NetworkPlayer np : partyMembers) {
            np.sendParsedMessage(text, formatMap);
        }
    }

    public void chat(NetworkPlayer sender, String content) {
        PartyChannel partyChannel = (PartyChannel) ChannelRegistry.getChannelByFunction(ChannelFunction.PARTY);

        if (partyChannel == null) {
            throw new NullPointerException("partyChannel is null! maybe my code sucks?");
        }

        if (sender.isMutingChannel(partyChannel)) {
            sender.sendParsedMessage(MessageGrabber.grab(MessageKey.CHAT_CHANNEL_IS_MUTED), MapFormatters.channelFormatter(partyChannel));
            return;
        }

        Map<String, Object> playerFormatter = MapFormatters.playerFormatter(sender);

        playerFormatter.put("content", content);
        playerFormatter.put("message", content);

        for (String uuid : getPlayers()) {
            NetworkPlayer np = NetworkPlayer.getPlayerByUUID(UUID.fromString(uuid));

            if (!np.isMutingChannel(partyChannel))
                np.sendParsedMessage(MessageGrabber.grab(MessageKey.PARTY_CHAT_MESSAGE), playerFormatter);
        }

        NetworkPlayer owner = NetworkPlayer.getPlayerByUUID(UUID.fromString(ownerUUID));

        if (!owner.isMutingChannel(partyChannel))
            owner.sendParsedMessage(MessageGrabber.grab(MessageKey.PARTY_CHAT_MESSAGE), playerFormatter);
    }
}
