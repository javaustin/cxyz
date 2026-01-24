package com.carrotguy69.cxyz.models.db;

import com.carrotguy69.cxyz.http.Request;
import com.carrotguy69.cxyz.other.utils.TimeUtils;

import java.util.*;

import static com.carrotguy69.cxyz.CXYZ.*;

public class FriendRequest {
    private final String sender;
    private final String recipient;
    private final long expireTimestamp;

    public FriendRequest(String sender, String recipient, long expireTimestamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.expireTimestamp = expireTimestamp;
    }

    public void delete() {
        Request.postRequest(api_endpoint + "/friendRequest/delete", gson.toJson(Map.of("sender", sender, "recipient", recipient, "expireTimestamp", expireTimestamp)));
    }

    public void create() {
        Request.postRequest(api_endpoint + "/friendRequest/create", gson.toJson(Map.of("sender", sender, "recipient", recipient, "expireTimestamp", expireTimestamp)));
    }

    public NetworkPlayer getSender() {
        return NetworkPlayer.getPlayerByUUID(UUID.fromString(sender));
    }

    public NetworkPlayer getRecipient() {
        return NetworkPlayer.getPlayerByUUID(UUID.fromString(recipient));
    }

    public UUID getSenderUUID() {
        return UUID.fromString(sender);
    }

    public UUID getRecipientUUID() {
        return UUID.fromString(recipient);
    }

    public long getExpireTimestamp() {
        return this.expireTimestamp;
    }

    public static FriendRequest getLastFriendRequest(NetworkPlayer sender, NetworkPlayer recipient) {
        // This assumes all of our network players should be initialized already.

        Collection<FriendRequest> requests = friendRequests.get(sender.getUUID());
        FriendRequest lastRequest = null;

        for (FriendRequest request : requests) {
            if (lastRequest == null) {
                // This means we can't compare our new request with the lastRequest.
                // So once we get a valid request that is not expired, we will set that as our last request.

                lastRequest = request; // <-- This value should never be null
            }

            else {
                // This means that the last request is something we can compare the current request (in the iterator) to.
                // So we can effectively sort it and replace it if our iterator one is more recent.
                if (lastRequest.getExpireTimestamp() < request.getExpireTimestamp() && request.getRecipient().getUUID().equals(recipient.getUUID())) {
                    // If the iterator request has a later timestamp than the lastRequest, and the recipient matches the argument in the function. -> To get the most recent

                    lastRequest = request;
                }

            }
        }

        // Finally, we will check that our timestamp not is expired, and our sender and recipient matches the top parameters.
        if (lastRequest != null && lastRequest.getRecipientUUID().equals(recipient.getUUID()) && lastRequest.getSenderUUID().equals(sender.getUUID()) && lastRequest.getExpireTimestamp() > TimeUtils.unixTimeNow()) {
            return lastRequest;
        }

        else {
            return null;
        }
    }

    public static void deleteFriendRequest(UUID senderUUID, UUID recipientUUID) {

        List<Map.Entry<UUID, FriendRequest>> toRemove = new ArrayList<>();

        for (Map.Entry<UUID, FriendRequest> entry : friendRequests.entries()) {
            if (entry.getKey().equals(senderUUID) && entry.getValue().getRecipientUUID().equals(recipientUUID)) {
                toRemove.add(entry);
            }
        }

        for (Map.Entry<UUID, FriendRequest> entry : toRemove) {
            friendRequests.remove(entry.getKey(), entry.getValue());
        }

    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", expireTimestamp='" + expireTimestamp + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof FriendRequest))
            return false;

        FriendRequest req = (FriendRequest) o;

        return Objects.equals(sender, req.sender)
            && Objects.equals(recipient, req.recipient)
            && expireTimestamp == req.expireTimestamp;
    }

}
