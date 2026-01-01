package com.carrotguy69.cxyz.classes.models.config;

import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.classes.exceptions.InvalidConfigException;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Cosmetic {

    private final String id;
    private final String display;
    private final String lore;
    private final CosmeticType type;
    private final PlayerRank rankRequirement;
    private final long levelRequirement;
    private final long price;
    private final boolean enabled;
    private Consumer<ActiveCosmetic> equipAction = ac -> {};
    private Consumer<ActiveCosmetic> unequipAction = ac -> {};

    public enum CosmeticType {
        // Chat tags, Chat colors, and rank plates, are the only types of cosmetics that are equipped via chat.
        CHAT_TAG,
        CHAT_COLOR,
        RANK_PLATE,
        WEARABLE,
        GADGET,
        MOVEMENT_TRAIL,
        PROJECTILE_TRAIL,
        KILL_EFFECT
    }

    public Cosmetic(String id, String display, String lore, CosmeticType type, long price, long levelRequirement, PlayerRank rankRequirement, boolean enabled) {
        this.id = id;
        this.display = display;
        this.lore = lore;
        this.type = type;
        this.price = price;
        this.levelRequirement = levelRequirement;
        this.rankRequirement = rankRequirement;
        this.enabled = enabled;
    }

    public CosmeticType getType() {
        return type;
    }

    public long getLevelRequirement() {
        return levelRequirement;
    }

    public PlayerRank getRankRequirement() {
        return rankRequirement;
    }

    public long getPrice() {
        return price;
    }

    public String getLore() {
        return lore;
    }

    public String getDisplay() {
        return f(display);
    }

    public String getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Cosmetic setEquipAction(Consumer<ActiveCosmetic> ac) {
        this.equipAction = ac;
        return this;
    }

    public Cosmetic setUnequipAction(Consumer<ActiveCosmetic> ac) {
        this.unequipAction = ac;
        return this;
    }

    public Consumer<ActiveCosmetic> getEquipAction() {
        return this.equipAction;
    }

    public Consumer<ActiveCosmetic> getUnequipAction() {
        return this.unequipAction;
    }

    @Override
    public String toString() {
        return "Cosmetic{" +
                "id='" + id + '\'' +
                ", displayName='" + display + '\'' +
                ", lore='" + lore + '\'' +
                ", type=" + type.name() +
                ", price=" + price +
                ", levelRequirement=" + levelRequirement +
                ", rankRequirement=" + rankRequirement.getName() +
                ", enabled=" + enabled +
                ", equipAction=" + equipAction.toString() +
                ", unequipAction=" + unequipAction.toString() +
                '}';
    }

    // STATIC FUNCTIONS

    public static List<Cosmetic> loadCosmetics() {
        ConfigurationSection section = cosmeticsYML.getConfigurationSection("cosmetics");

        List<Cosmetic> results = new ArrayList<>();

        if (section != null) {
            for (String id : section.getKeys(false)) { /* Should get all rank names (default, vip, ...)*/ try {

                ConfigurationSection cosmeticData = section.getConfigurationSection(id);

                if (cosmeticData != null) {
                    String display = cosmeticData.getString("display");
                    String lore = cosmeticData.getString("lore");
                    CosmeticType type = CosmeticType.valueOf(cosmeticData.getString("type"));
                    long price = cosmeticData.getLong("price");
                    long levelRequirement = cosmeticData.getLong("level-requirement");
                    PlayerRank rankRequirement = PlayerRank.getRankByName(cosmeticData.getString("rank-requirement", "default"));
                    boolean enabled = cosmeticData.getBoolean("enabled");

                    Cosmetic csm = new Cosmetic(id, display, lore, type, price, levelRequirement, rankRequirement, enabled);

                    results.add(csm);
                }

                } catch (Exception ex) {throw new InvalidConfigException("cosmetics.yml", id, ex.getMessage());}
            }
        }

        if (results.isEmpty()) {
            Logger.warning("No cosmetics defined in cosmetics.yml, Ignoring!");
        }

        return results;
    }

    public static Cosmetic getCosmetic(String id) {
        for (Cosmetic cosmetic : cosmetics) {
            if (cosmetic.getId().equalsIgnoreCase(id)) {
                return cosmetic;
            }
        }

        return null;
    }
}
