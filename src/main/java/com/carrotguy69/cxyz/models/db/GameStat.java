package com.carrotguy69.cxyz.models.db;

import com.carrotguy69.cxyz.http.Request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.carrotguy69.cxyz.CXYZ.*;

public class GameStat {

    private final String uuid;
    private final String statID;
    private String value;
    public int version;

    public GameStat(UUID uuid, String statID, String value, int version) {
        this.uuid = uuid.toString();
        this.statID = statID;
        this.value = value;
        this.version = version;
    }

    public static List<GameStat> getStats(UUID playerUUID) {
        Collection<GameStat> stats = statUUIDMap.get(playerUUID);

        return new ArrayList<>(stats);
    }

    public static GameStat getStat(UUID playerUUID, String statID) {

        for (GameStat stat : getStats(playerUUID)) {
            if (statID.equalsIgnoreCase(stat.statID))
                return stat;
        }

        return null;
    }

    public void setStat() {
        ArrayList<GameStat> toRemove = new ArrayList<>();

        for (Map.Entry<UUID, GameStat> entry : statUUIDMap.entries()) {
            GameStat stat = entry.getValue();

            if (statID.equalsIgnoreCase(stat.statID)) {
                toRemove.add(stat);
            }
        }

        for (GameStat stat : toRemove) {
            statUUIDMap.remove(stat.getUUID(), stat);
        }

        statUUIDMap.put(this.getUUID(), this);
    }

    public UUID getUUID() {
        return UUID.fromString(uuid);
    }

    public String getStatID() {
        return statID;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "GameStat{uuid=" + uuid + "," +
        "statID=" + statID + "," +
        "value=" + value + "," +
        "version=" + version + "}";
    }

    public void sync() {
        this.version += 1;
        Request.postRequest(apiEndpoint + "/gameStat/set", gson.toJson(this));
    }
}
