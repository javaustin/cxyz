package com.carrotguy69.cxyz.models.config.cosmetics;

import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

import static com.carrotguy69.cxyz.CXYZ.*;

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
                cosmetic.getLevelRequirement(),
                cosmetic.getRankRequirement(),
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

    public NetworkPlayer getPlayer() {
        return player;
    }

    public ActiveCosmetic addTask(BukkitTask task) {
        tasks.add(task);

        return this;
    }

    public ActiveCosmetic removeTask(BukkitTask task) {
        tasks.remove(task);

        return this;
    }

    public ActiveCosmetic equip() {

        activeCosmeticMap.computeIfAbsent(this.player.getUUID(), k -> new ArrayList<>()).add(this);

        switch (this.getType()) {
            case CHAT_TAG:
                this.player.setChatTag(this.getDisplay());
                break;

            case CHAT_COLOR:
                this.player.setChatColor(this.getDisplay());
                break;

            case RANK_PLATE:
                this.player.setCustomRankPlate(this.getDisplay());
                break;

            default:
                originalCosmetic.getEquipAction().accept(this);
        }

        return this;
    }

    public ActiveCosmetic unEquip() {

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


                for (BukkitTask task : tasks) {
                    task.cancel();
                }

                tasks.clear();

                originalCosmetic.getUnequipAction().accept(this);
        }

        List<ActiveCosmetic> list = activeCosmeticMap.get(this.player.getUUID());
        if (list != null) list.remove(this);

        return this;
    }

    public static boolean isEquippedTo(ActiveCosmetic activeCosmetic, NetworkPlayer np) {
        for (ActiveCosmetic ac : activeCosmeticMap.get(np.getUUID())) {
            if (ac.getId().equalsIgnoreCase(activeCosmetic.getId()))
                return true;
        }

        return false;
    }


    public static void loadActiveCosmetics() {
        // We will likely use this script to register behavior for each custom cosmetic.
        // I think it would actually be okay to call this from the Startup class.


        // ------------------------------------------------------- RAINBOW-ARMOR -------------------------------------------------------


        Cosmetic rainbowArmor = Cosmetic.getCosmetic("rainbow-armor");

        if (rainbowArmor == null) {
            Logger.warning("rainbowArmor could not be equipped. (null)");
            return;
        }

        // rainbow-armor already exists and is loaded by the config, but there are not any actions added.
        rainbowArmor.setEquipAction(ac -> {
            Player p = ac.getPlayer().getPlayer();

            ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
            ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
            ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS, 1);
            ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);

            p.getInventory().setArmorContents(List.of(helmet, chest, legs, boots).toArray(new ItemStack[0]));

            LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
            LeatherArmorMeta chestMeta = (LeatherArmorMeta) chest.getItemMeta();
            LeatherArmorMeta legsMeta = (LeatherArmorMeta) legs.getItemMeta();
            LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();

            if (helmetMeta == null || chestMeta == null || legsMeta == null || bootsMeta == null) {
                Logger.warning("Failed to apply item meta to rainbow armor. getItemMeta() returned null!");
                return;
            }

            for (ItemMeta meta : List.of(helmetMeta, chestMeta, legsMeta, bootsMeta)) {
                meta.setLore(List.of(f(ac.originalCosmetic.getLore()).split("\n")));
            }

            helmetMeta.setDisplayName(f("&cR&6a&ei&an&bb&9o&dw &cH&6e&el&am&be&9t")); // In practice, we will not allow players to remove their armor when applied as a cosmetic. (update)
            chestMeta.setDisplayName(f("&cR&6a&ei&an&bb&9o&dw &cC&6h&ee&as&bt&9p&dl&ca&6t&ee"));
            legsMeta.setDisplayName(f("&cR&6a&ei&an&bb&9o&dw &cL&6e&eg&ag&bi&9n&dg&cs"));
            bootsMeta.setDisplayName(f("&cR&6a&ei&an&bb&9o&dw &cB&6o&eo&at&bs"));

            p.getInventory().setHelmet(helmet);
            p.getInventory().setChestplate(chest);
            p.getInventory().setLeggings(legs);
            p.getInventory().setBoots(boots);

            // Track hue from 0-255
            final int[] hue = {0};

            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (this.isCancelled()) {
                        return;
                    }

                    // Convert hue (0-255) to 0.0-1.0 for HSB
                    float h = hue[0] / 255f;

                    // Convert HSB to RGB and strip alpha
                    int rgb = java.awt.Color.HSBtoRGB(h, 1f, 1f) & 0xFFFFFF;
                    Color color = Color.fromRGB(rgb);

                    // Apply color to armor
                    helmetMeta.setColor(color);
                    helmet.setItemMeta(helmetMeta);

                    chestMeta.setColor(color);
                    chest.setItemMeta(chestMeta);

                    legsMeta.setColor(color);
                    legs.setItemMeta(legsMeta);

                    bootsMeta.setColor(color);
                    boots.setItemMeta(bootsMeta);

                    p.getInventory().setHelmet(helmet);
                    p.getInventory().setChestplate(chest);
                    p.getInventory().setLeggings(legs);
                    p.getInventory().setBoots(boots);

                    // Increment hue
                    hue[0] += 2;
                    if (hue[0] > 255) hue[0] = 0; // wrap around
                }
            }.runTaskTimer(plugin, 0L, 2L);


            taskIDs.add(task.getTaskId()); // adds task id to the core task manager for it to be canceled upon restart/plugin reload.
            ac.addTask(task); // adds to the specific ActiveCosmetic task list (can be cancelled on .unEquip())
        })
        .setUnequipAction(ac -> {
            Player p = ac.getPlayer().getPlayer();

            p.getInventory().setHelmet(null);
            p.getInventory().setChestplate(null);
            p.getInventory().setLeggings(null);
            p.getInventory().setBoots(null);

            // The class cancels the original tasks for us, provided we add it to the list. This should be all?
        });

        // With this, our actions are registered and will be called by NetworkPlayer.equipCosmetic(Cosmetic cosmetic); when prompted (by command or server join)
        // For simple cosmetics (chat tag, chat color, rank nameplate), we simply won't include them in this driver, we won't set actions.
        // The simple equip/unequip logic doesn't even touch the consumers.


        // ------------------------------------------------------- GRAPPLE-ROD -------------------------------------------------------

        Cosmetic grappleRod = Cosmetic.getCosmetic("grapple-rod");

        if (grappleRod == null) {
            Logger.warning("grappleRod could not be equipped. (null)");
            return;
        }

        grappleRod.setEquipAction(ac -> {
            Player p = ac.getPlayer().getPlayer();

            ItemStack rod = new ItemStack(Material.FISHING_ROD, 1);

            ItemMeta rodMeta = rod.getItemMeta();

            if (rodMeta != null) {
                rodMeta.setDisplayName(f(ac.getDisplay()));
                rodMeta.setLore(Arrays.asList(f(ac.getLore()).split(" ")));
            }

            rod.setItemMeta(rodMeta);

            p.getInventory().addItem(rod);
        });

        grappleRod.setUnequipAction(ac -> {
            Player p = ac.getPlayer().getPlayer();

            for (int i = 0; i < p.getInventory().getSize(); i++) {
                ItemStack is = p.getInventory().getItem(i);

                if (is != null && is.getItemMeta() != null && is.getItemMeta().getDisplayName().equalsIgnoreCase(f(ac.getDisplay()))) {
                    p.getInventory().setItem(i, null);
                }
            }
        });

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


    }

}
