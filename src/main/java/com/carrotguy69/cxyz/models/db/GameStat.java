package com.carrotguy69.cxyz.models.db;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.http.Request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.carrotguy69.cxyz.CXYZ.*;

public class GameStat {

    private final UUID uuid;
    private final String statID;
    private String value;
    public int version;

    private GameStat(UUID uuid, String statID, String value) {
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
        GameStat stat = GameStat.getStat(playerUUID, statID);

        if (stat == null) {
            stat = new GameStat(playerUUID, statID, value);
        }

        if (statUUIDMap.containsEntry(playerUUID, stat)) {
            stat.setValue(value);
            stat.version += 1;
        }

        else {
            statUUIDMap.put(playerUUID, stat);
        }

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

    private void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "GameStat{" +
        "uuid=" + uuid + "," +
        "statID=" + statID + "," +
        "value=" + value + "," +
        "version=" + version + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GameStat))
            return false;

        GameStat otherStat = (GameStat) obj;

        return this.uuid == otherStat.uuid && Objects.equals(this.statID, otherStat.statID);
    }

    public void sync() {
        this.version += 1;
        Request.postRequest(apiEndpoint + "/gameStat/set", gson.toJson(this));
    }
}
