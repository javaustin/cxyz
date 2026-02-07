package com.carrotguy69.cxyz.models.config.cosmetics;

import com.carrotguy69.cxyz.cmd.general.ChatColor;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import static com.carrotguy69.cxyz.cmd.general.ChatColor.getColor;

public class ActiveCosmetic extends Cosmetic {

    public static Map<UUID, List<ActiveCosmetic>> activeCosmeticMap = new HashMap<>();

    private final List<BukkitTask> tasks;
    private final NetworkPlayer player;
    private final Cosmetic originalCosmetic;

    public ActiveCosmetic(Cosmetic cosmetic, NetworkPlayer player) {
        super(
                cosmetic.getId(),
                cosmetic.getDisplay(),
                cosmetic.getLore(),
                cosmetic.getType(),
                cosmetic.getPrice(),
                cosmetic.getRequiredLevel(),
                cosmetic.getRequiredRank(),
                cosmetic.isEnabled()
        );

        this.originalCosmetic = cosmetic;
        this.tasks = new ArrayList<>();
        this.player = player;

        cosmetic.listeners.forEach((eventClass, list) ->
                listeners.put(eventClass, new ArrayList<>(list))
        );
    }

    @Override
    public String toString() {
        return "ActiveCosmetic{" +
                " cosmetic=" + originalCosmetic.toString()  + // get the original Cosmetic and call its toString()
                ", player(UUID)='" + player.getUUID() + '\'' +
                ", tasks=" + tasks.toString() +
                "}";
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void handleEvent(T event) {
        List<CosmeticListener<?>> list = listeners.get(event.getClass());

        if (list == null) return;

        for (CosmeticListener<?> raw : list) {
            ((CosmeticListener<T>) raw).handle(event, this);
        }
    }

    public List<BukkitTask> getTasks() {
        return tasks;
    }

    public NetworkPlayer getNetworkPlayer() {
        return player;
    }

    public void addTask(BukkitTask task) {
        tasks.add(task);

    }

    public ActiveCosmetic removeTask(BukkitTask task) {
        tasks.remove(task);

        return this;
    }

    public void equip() {


        activeCosmeticMap.computeIfAbsent(this.player.getUUID(), k -> new ArrayList<>()).add(this);
        originalCosmetic.getEquipAction().accept(this);

        switch (this.getType()) {
            case CHAT_TAG:
                this.player.setChatTag(this.getDisplay());
                break;

            case CHAT_COLOR:
                // The Cosmetic class is limited because it only accepts one parameter to define the display of an object.
                // In almost all other cosmetic types, the display of the cosmetic equipped can be functionally equal to the shop interface name (if you will).
                // But for cosmetics of the CHAT_COLOR type particularly, the singular `display` variable cannot capture the actual color code, as well as the name,
                // or else the name of the color would display every time you typed something.

                // So to work around this issue, we use the color map as defined in config.yml. Its keys are the registered names of the colors (e.g. 'blue', 'dark_red'), and the values are the corresponding color codes.
                // This map also supports custom colors to be integrated with the plugin, so you can define 'fancy_purple' and map that value to '&x&a&b&6&5&c&e'.
                // Then you can look up colors by that custom name, an extension of already existing legacy colors.

                String value = this.getDisplay().strip();


                ChatColor.Color color = getColor(value);

                if (color == null) {
                    this.player.setChatColor("");
                    break;
                }

                this.player.setChatColor(color.code);

                break;

            case RANK_PLATE:
                this.player.setCustomRankPlate(this.getDisplay());
                break;

            default:
                originalCosmetic.getEquipAction().accept(this);
                break;
        }
    }

    public void unEquip() {

        Logger.debugUser(this.getType().toString());
        switch (this.getType()) {
            case CHAT_TAG:
                this.player.setChatTag("");
                break;

            case CHAT_COLOR:
                this.player.setChatColor("");
                break;

            case RANK_PLATE:
                this.player.setCustomRankPlate("");
                break;

            default:
                originalCosmetic.getUnequipAction().accept(this); // moved

                for (BukkitTask task : tasks) {
                    task.cancel();
                }

                tasks.clear();

        }

        List<ActiveCosmetic> list = activeCosmeticMap.get(this.player.getUUID());
        if (list != null) list.remove(this);

    }


    public static void loadActiveCosmetics() {
        new ActiveCosmeticLoader().load();
    }
}
