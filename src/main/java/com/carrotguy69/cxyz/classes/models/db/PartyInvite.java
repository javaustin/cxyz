package com.carrotguy69.cxyz.classes.models.db;

import com.carrotguy69.cxyz.classes.http.Requests;
import com.carrotguy69.cxyz.other.TimeUtils;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static com.carrotguy69.cxyz.CXYZ.*;

public class PartyInvite {

    private final String inviter;
    private final String recipient;
    private final long expireTimestamp;

    /*
    PartyInvite

    A partyInvite is an inviter, recipient, and expireTimestamp which represents an active invite to a party.

    Our responsibilities are to:
    - Fetch a party invite from the API and/or static Map given a inviterName.
    - Enter a party invite into the partyInvites table when /party invite <player> is run.
    - Delete a party invite after it has been accepted.
    - Delete a party invite after it expired.
    */


    public PartyInvite(String inviter, String recipient, long expireTimestamp) {
        this.inviter = inviter;
        this.recipient = recipient;
        this.expireTimestamp = expireTimestamp;
    }

    public void delete() {
        Requests.postRequest(api_endpoint + "/partyInvite/delete", gson.toJson(Map.of("inviter", inviter, "recipient", recipient, "expireTimestamp", expireTimestamp)));
    }

    public void create() {
        Requests.postRequest(api_endpoint + "/partyInvite/create", gson.toJson(Map.of("inviter", inviter, "recipient", recipient, "expireTimestamp", expireTimestamp)));
    }

//    public void sync() {
//        Requests.postRequest(api_endpoint + "/partyInvite/sync", gson.toJson(Map.of("inviter", inviter, "recipient", recipient, "expireTimestamp", expireTimestamp)));
//    }

    public NetworkPlayer getInviter() {
        return NetworkPlayer.getPlayerByUUID(UUID.fromString(inviter));
    }

    public NetworkPlayer getRecipient() {
        return NetworkPlayer.getPlayerByUUID(UUID.fromString(recipient));
    }

    public UUID getInviterUUID() {
        return UUID.fromString(inviter);
    }

    public UUID getRecipientUUID() {
        return UUID.fromString(recipient);
    }

    public long getExpireTimestamp() {
        return this.expireTimestamp;
    }

    public static PartyInvite getLastInvite(NetworkPlayer inviter, NetworkPlayer recipient) {
        // This assumes all of our network players should be initialized already.

        Collection<PartyInvite> invites = partyInvites.get(inviter.getUUID());
        PartyInvite lastInvite = null;

        for (PartyInvite invite : invites) {
            if (lastInvite == null) {
                // This means we can't compare our new invite with the lastInvite.
                // So once we get a valid invite that is not expired, we will set that as our last invite.

                lastInvite = invite; // <-- This value should never be null
            }

            else {
                // This means that the last invite is something we can compare the current invite (in the iterator) to.
                // So we can effectively sort it and replace it if our iterator one is more recent.
                if (lastInvite.getExpireTimestamp() < invite.getExpireTimestamp() && invite.getRecipient().getUUID().equals(recipient.getUUID())) {
                    // If the iterator invite has a later timestamp than the lastInvite, and the recipient matches the argument in the function. -> To get the most recent

                    lastInvite = invite;
                }

            }
        }

        // Finally, we will check that our timestamp not is expired, and our inviter and recipient matches the top parameters.
        if (lastInvite != null && lastInvite.getRecipientUUID().equals(recipient.getUUID()) && lastInvite.getInviterUUID().equals(inviter.getUUID()) && lastInvite.getExpireTimestamp() > TimeUtils.unixTimeNow()) {
            return lastInvite;
        }

        else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "PartyInvite{" +
                "inviter='" + inviter + '\'' +
                ", recipient='" + recipient + '\'' +
                ", expireTimestamp=" + expireTimestamp +
                '}';
    }

}
