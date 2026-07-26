package com.carrotguy69.cxyz.models.db;

import com.carrotguy69.cxyz.http.Request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.carrotguy69.cxyz.CXYZ.*;

public class GameStat {

    private final UUID uuid;
    private final String statID;
    private String value;
    public int version;

    public GameStat(UUID uuid, String statID, String value) {
        this.uuid = uuid;
        this.statID = statID;
        this.value = value;
        this.version = 0;
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

    public static GameStat setStat(UUID playerUUID, String statID, String value) {
        ArrayList<GameStat> toRemove = new ArrayList<>();

        for (GameStat stat : statUUIDMap.get(playerUUID)) {
            if (statID.equalsIgnoreCase(stat.statID)) {
                toRemove.add(stat);
            }
        }

        for (GameStat stat : toRemove) {
            statUUIDMap.remove(stat.getUUID(), stat);
        }

        GameStat stat = new GameStat(playerUUID, statID, value);

        statUUIDMap.put(playerUUID, stat);

        return stat;
    }

    public static GameStat setStat(GameStat gameStat) {
        return setStat(gameStat.getUUID(), gameStat.getStatID(), gameStat.getValue());
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getStatID() {
        return statID;
    }

    public String getValue() {
        return value;
    }
//
//    public void setValue(String value) {
//        this.value = value;
//    }

    @Override
    public String toString() {
        return "GameStat{" +
        "uuid=" + uuid + "," +
        "statID=" + statID + "," +
        "value=" + value + "," +
        "version=" + version + "}";
    }

    public void sync() {
        this.version += 1;
        Request.postRequest(apiEndpoint + "/gameStat/set", gson.toJson(this));
    }
}
