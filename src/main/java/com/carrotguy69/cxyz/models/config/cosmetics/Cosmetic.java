package com.carrotguy69.cxyz.models.config.cosmetics;

import com.carrotguy69.cxyz.models.config.PlayerRank;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.exceptions.InvalidConfigException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Cosmetic {
    /*
    we are in a pickle with ActiveCosmetic event handling. i am already unfamiliar with this as we go so this should be done simply and effectively.
    so in a script, i like this functionality where we can register an event to the ActiveCosmetic. this should stay as is, similar to format as equip and unequip actions:

        // this works
        grappleRod.setEquipAction((ac -> {
            Player p = ac.getPlayer().getPlayer();
            ...
        }));

        // this does not
        grappleRod.on(PlayerFishEvent.class, ((event, ac) -> {
            if (event.getState() != PlayerFishEvent.State.REEL_IN)
                return;

            Player p = ac.getPlayer().getPlayer();

            Vector pull = event.getHook()
                    .getLocation()
                    .toVector()
                    .subtract(p.getLocation().toVector())
                    .normalize()
                    .multiply(2);

            p.playSound(p, Sound.ENTITY_BLAZE_SHOOT, 0.5f, 1.0f);
            p.setVelocity(pull);

        }));

        however grappleRod is a Cosmetic, not an ActiveCosmetic, and it must stay that way because we cannot use ActiveCosmetic in the loader script, a player just does not exist, thats why the previous ocnsumer for setEquipAction() exists.
        if we refactor our listener (public interface CosmeticListener<T extends Event> {void handle(T event, Cosmetic cosmetic);}) to accept ActiveCosmetic instead of Cosmetic it seems like we wont be able to register it either (because then we would have to cast in the Cosmetic class - still no player)

        what is a possible solution to this that also allows:
        1) a similar format to the former .setEquipAction()
        2) functionality to add listeners to a cosmetic
        3) lives in Cosmetic, ActiveCosmetic is only used when a player joins or equips a cosmetic

     */

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

    public final Map<Class<? extends Event>, List<CosmeticListener<?>>> listeners = new HashMap<>();

    public <T extends Event> void on(Class<T> eventClass, CosmeticListener<T> listener) {
        listeners.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(listener);
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

    public static List<Cosmetic> getCosmetics() {
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

                    if (!enabledCosmeticTypes.contains(type)) {
                        continue;
                    }

                    results.add(csm);
                }

                else {
                    Logger.warning(String.format("Cosmetic %s did not load (configuration section not found).", id));
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
