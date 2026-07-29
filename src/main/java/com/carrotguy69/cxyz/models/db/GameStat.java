package com.carrotguy69.cxyz.models.db;

import com.carrotguy69.cxyz.http.Request;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.carrotguy69.cxyz.CXYZ.*;

public class GameStat {

    public static final Multimap<String, GameStat> statIDMap = ArrayListMultimap.create();

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

    public static Collection<GameStat> getStatsByID(String key) {
        return statIDMap.get(key);
    }

    public static Collection<GameStat> getStatsByUUID(UUID playerUUID) {
        return statUUIDMap.get(playerUUID);
    }

    public static Map<Integer, GameStat> getStatLeaderboard(String key) {
        // This will only sort numbers, but will not error when non-numbers are encountered.

        List<GameStat> stats;
        Map<Integer, GameStat> map = new HashMap<>();

        stats = getStatsByID(key).stream().sorted(Comparator.comparingDouble(stat -> parseDoubleOrNot(stat.getValue()))).collect(Collectors.toList()).reversed();


        for (int i = 0; i < stats.size(); i++) {
            map.put(i + 1, stats.get(i));
        }

        return map;
    }

    private static double parseDoubleOrNot(String s) {
        try {
            return Double.parseDouble(s);
        }
        catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static int getStatRanking(UUID uuid, String statID) {
        Map<Integer, GameStat> gameStatsRanked = GameStat.getStatLeaderboard(statID);

        for (Map.Entry<Integer, GameStat> entry : gameStatsRanked.entrySet()) {
            if (entry.getValue().getUUID().equals(uuid)) {
                return entry.getKey();
            }
        }

        return gameStatsRanked.size();
    }

    public static GameStat getStat(UUID playerUUID, String statID) {
        // We can search through the statID map or the statUUID map.
        // We will try whichever is smaller so we can perform fewer comparisons.

        if (statIDMap.size() < statUUIDMap.size()) {
            for (GameStat stat : getStatsByID(statID)) {
                if (stat.getUUID().equals(playerUUID)) {
                    return stat;
                }
            }
        }

        else {
            for (GameStat stat : getStatsByUUID(playerUUID)) {
                if (statID.equalsIgnoreCase(stat.statID))
                    return stat;
            }
        }

        return null;
    }

    public static GameStat setStat(UUID playerUUID, String statID, String value) {
        GameStat stat = GameStat.getStat(playerUUID, statID);

        if (stat == null) {
            stat = new GameStat(playerUUID, statID, value);
        }

        if (statUUIDMap.containsEntry(playerUUID, stat)) {
            stat.value = value;
            stat.version += 1;
        }
        else {
            statUUIDMap.put(playerUUID, stat);
            statIDMap.put(statID, stat);
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
