package com.carrotguy69.cxyz.classes.models.db;

import com.carrotguy69.cxyz.classes.http.Requests;

import java.util.Map;
import java.util.UUID;

import static com.carrotguy69.cxyz.CXYZ.*;

public class PartyExpire {
    private final String uuid;
    private long timestamp;

    /*
    PartyExpire

    A partyExpire is a UUID and timestamp which represents at what time any given player will be auto-kicked from a party.

    Our responsibilities are to:
    - Enter a player into the partyExpires table & map when they leave.
    - Cancel (delete) a partyExpire if the player joins back within the allotted time.
    - Periodically check partyExpires for expired players (where Time.unixTimeNow() >= timestamp + 300)
    - Auto remove a player from a party if they are expired.
    */

    @Override
    public String toString() {
        return String.format("PartyExpire{uuid=%s,timestamp=%d}", uuid, timestamp);
    }

    public PartyExpire(String uuid, long timestamp) {
        this.uuid = uuid;
        this.timestamp = timestamp;
    }

    public void create() {
        if (!partyExpires.containsKey(UUID.fromString(uuid))) { // Must uphold unique constraint

            Requests.postRequest(api_endpoint + "/partyExpire/create", gson.toJson(Map.of("uuid", uuid, "timestamp", timestamp)));
        }

        else {
            this.sync();
        }

    }

    public void sync() {
        Requests.postRequest(api_endpoint + "/partyExpire/sync", gson.toJson(Map.of("uuid", uuid, "timestamp", timestamp)));
    }

    public void delete() {
        Requests.postRequest(api_endpoint + "/partyExpire/delete", gson.toJson(Map.of("uuid", uuid)));
    }

    public UUID getUUID() {
        return UUID.fromString(uuid);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
