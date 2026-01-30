package com.carrotguy69.cxyz.models.db;

import com.carrotguy69.cxyz.http.Request;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.other.utils.TimeUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Punishment {

    private long id;
    private String uuid;
    private String username;
    private String modUUID;
    private String modUsername;
    private String type;
    private long issuedTimestamp;
    private long effectiveUntilTimestamp;
    private long expireTimestamp;
    private String reason;
    private int enforced;
    private String editorModUUID;
    private String editorModUsername;

    public enum PunishmentType {
        WARN,
        MUTE,
        KICK,
        BAN
    }

    public long getID() {
        return id;
    }

    public String getUUID() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getModUUID() {
        return modUUID;
    }

    public String getModUsername() {
        return modUsername;
    }

    public PunishmentType getType() {
        return PunishmentType.valueOf(type);
    }

    public long getIssuedTimestamp() {
        return issuedTimestamp;
    }

    public long getEffectiveUntilTimestamp() {
        return effectiveUntilTimestamp;
    }

    public long getExpireTimestamp() {
        return expireTimestamp;
    }

    public String getReason() {
        return reason;
    }

    public boolean isEnforced() {
        return enforced == 1;
    }

    public String getEditorModUUID() {
        return editorModUUID;
    }

    public String getEditorModUsername() {
        return editorModUsername;
    }

    @Override
    public String toString() {
        return "Punishment{" +
                "id=" + id +
                ",uuid=" + uuid +
                ",username=" + username +
                ",modUUID=" + modUUID +
                ",modUsername=" + modUsername +
                ",type=" + type +
                ",issuedTimestamp=" + issuedTimestamp +
                ",effectiveUntilTimestamp=" + effectiveUntilTimestamp +
                ",expireTimestamp=" + expireTimestamp +
                ",reason=" + reason +
                ",enforced=" + enforced +
                ",editorModUUID=" + editorModUUID +
                ",editorModUsername=" + editorModUsername +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Punishment))
            return false;

        Punishment pun = (Punishment) o;

        return Objects.equals(id, pun.id)
                && Objects.equals(uuid, pun.uuid)
                && Objects.equals(modUUID, pun.modUUID)
                && Objects.equals(issuedTimestamp, pun.issuedTimestamp);
    }

    public boolean isPermanent() {
        return effectiveUntilTimestamp == -1;
    }


    public boolean hasDuration() {
        return !Objects.equals(this.getType(), PunishmentType.MUTE) || !Objects.equals(this.getType(), PunishmentType.BAN);
    }

    public void setID(long id) {
        this.id = id;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setModUsername(String modUsername) {
        this.modUsername = modUsername;
    }

    public void setModUUID(String modUUID) {
        this.modUUID = modUUID;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setEffectiveUntilTimestamp(long effectiveUntilTimestamp) {
        this.effectiveUntilTimestamp = effectiveUntilTimestamp;
    }

    public void setExpireTimestamp(long expireTimestamp) {
        this.expireTimestamp = expireTimestamp;
    }

    public void setIssuedTimestamp(long issuedTimestamp) {
        this.issuedTimestamp = issuedTimestamp;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setEnforced(boolean enforced) {
        this.enforced = (enforced ? 1 : 0);
    }

    public void setEditorMod(NetworkPlayer np) {
        setEditorModUUID(np.getUUID().toString());
        setEditorModUsername(np.getUsername());
    }

    public void setEditorModUUID(String editorModUUID) {
        this.editorModUUID = editorModUUID;
    }

    public void setEditorModUsername(String username) {
        this.editorModUsername = username;
    }

    public void create() {
        punishmentIDMap.put(this.getID(), this);

        Map<String, Object> body = new HashMap<>();
        body.put("punishment", this);
        // Hassle-free "wrapper" ^

        Request.postRequest(apiEndpoint + "/punishment/set", gson.toJson(body));
    }

    public void edit() {
        punishmentIDMap.put(this.getID(), this);

        Map<String, Object> body = new HashMap<>();
        body.put("punishment", this);
        // Hassle-free "wrapper" ^

        Request.postRequest(apiEndpoint + "/punishment/edit", gson.toJson(body));
    }

    public void delete() {
        punishmentIDMap.remove(this.getID(), this);

        Request.postRequest(apiEndpoint + "/punishment/delete", gson.toJson(Map.of("id", this.id)));
    }

    // Static methods
    public static Punishment getLastGlobalPunishment() {
//        List<Punishment> punishments = new ArrayList<>(punishmentIDMap.values());
//
//        punishments.sort(Comparator.comparing(Punishment::getIssuedTimestamp));
//
//        if (punishments.isEmpty()) {
//            Logger.warning("No previous punishments were detected by the API. If you haven't assigned punishments on this server before, you may ignore this message.");
//            return null;
//        }
//
//        return punishments.getFirst();

        return punishmentIDMap.values().stream()
                .sorted(
                        Comparator.comparingLong(Punishment::getIssuedTimestamp)
                        .thenComparing(Punishment::getID)
                )
                .collect(Collectors.toList())
                .getFirst();
    }

    // We are 99% sure that our ID is correct. In the case that it isn't, it is corrected by the backend.
    // When a player joins again or a mod looks up the punishment after the original event, the message is regenerated by design - and provides 100% accurate info.

    // Next let's explore how the punishment actually gets updated. (punishmentDelivery). If we think the id is 22, but the backend tells is it's actually 23,
    // the backend probably needs to recache all the punishments.
    public static long generateID() {
        Punishment lastPunishment = getLastGlobalPunishment();

        if (lastPunishment == null) {
            Logger.debugPunishment("No last punishment found (returns 1)");
            return 1;
        }

        Logger.debugPunishment("lastGlobalPunishment: " + lastPunishment.toString());

        return lastPunishment.getID() + 1;
    }

    public static List<Punishment> getPlayerPunishments(NetworkPlayer np) {
        List<Punishment> list = new ArrayList<>();

        for (Map.Entry<Long, Punishment> entry : punishmentIDMap.entrySet()) {
            if (Objects.equals(entry.getValue().getUUID(), np.getUUID().toString())) {
                list.add(entry.getValue());
            }
        }

        return list;
    }

    public static List<Punishment> getActivePunishments(NetworkPlayer np, PunishmentType type) {

        Collection<Punishment> playerPunishments = getPlayerPunishments(np);

        List<Punishment> res = new ArrayList<>();

        for (Punishment punishment : playerPunishments) {
            if ((punishment.getEffectiveUntilTimestamp() > TimeUtils.unixTimeNow() || punishment.isPermanent()) && punishment.isEnforced() && punishment.getType() == type) { // -1 Resembles a permanent ban
                res.add(punishment);
            }
        }

        return res;
    }

    public static Punishment getActivePunishment(NetworkPlayer np, PunishmentType type) {
        Optional<Punishment> latest = getActivePunishments(np, type)
                .stream().max(Comparator.comparingLong(Punishment::getIssuedTimestamp));


        return latest.orElse(null);
    }


}
