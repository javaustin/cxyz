package com.carrotguy69.cxyz.models.config.cosmetics;

import com.carrotguy69.cxyz.exceptions.InvalidConfigException;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.other.utils.NotePitch;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;

import static com.carrotguy69.cxyz.CXYZ.f;
import static com.carrotguy69.cxyz.CXYZ.plugin;
import static com.carrotguy69.cxyz.CXYZ.random;
import static com.carrotguy69.cxyz.CXYZ.taskIDs;

public class ActiveCosmeticLoader {

    public void load() {
        // This is a long script for loading and implementing any custom cosmetic items.
        // Below is a brief example on how to use the Cosmetic class to set equip actions and events to a cosmetic.

        /*

        Cosmetic exampleCosmetic = Cosmetic.getCosmetic("example-cosmetic-id");

        // Setting equip/unequip actions
        exampleCosmetic.setEquipAction(activeCosmetic -> {
            // For this example we will give the player a diamond

            Player p = activeCosmetic.getNetworkPlayer().getPlayer();

            p.getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
        });

        exampleCosmetic.setUnequipAction(activeCosmetic -> {
            // Take the diamond away

            Player p = activeCosmetic.getNetworkPlayer().getPlayer();

            for (int i = 0; i < p.getInventory().getSize(); i++) {
                ItemStack is = p.getInventory().getItem(i);

                if (is != null && is.getType() == Material.DIAMOND) {
                    p.getInventory().setItem(i, null);
                }
            }

        });

        // Implement custom listeners
        rainbowArmor.on(PlayerDropItemEvent.class, ((event, ac) -> {
            // We will cancel the player from dropping the diamond

            ItemStack itemStack = event.getItemDrop().getItemStack();

            if (itemStack.getType() == Material.DIAMOND) {
                event.setCancelled(true);
            }
        }));

        // Ensure for any custom listener, that the event listener of that class is registered, and the execution is handled.

        // In Main.java
        @EventHandler
        public void onPlayerDropItem(PlayerDropItemEvent event) {
            List<ActiveCosmetic> activeCosmetics = ActiveCosmetic.activeCosmeticMap.get(event.getPlayer().getUniqueId());

            for (ActiveCosmetic ac : activeCosmetics) {
                ac.handleEvent(event);
            }
        }


         */

        // ------------------------------------------------------- RAINBOW-ARMOR -------------------------------------------------------


        Cosmetic rainbowArmor = Cosmetic.getCosmetic("rainbow-armor");

        if (rainbowArmor == null) {
            Logger.severe("ActiveCosmetics could not be loaded because there was an exception in the startup (ActiveCosmeticLoader.load()).");
            throw new InvalidConfigException("cosmetics.yml", "cosmetics", "could not find expected cosmetic 'rainbow-armor'");
        }

        List<String> rainbowDisplayNames = List.of(f("&cR&6a&ei&an&bb&9o&dw &cH&6e&el&am&be&9t"), f("&cR&6a&ei&an&bb&9o&dw &cC&6h&ee&as&bt&9p&dl&ca&6t&ee"), f("&cR&6a&ei&an&bb&9o&dw &cL&6e&eg&ag&bi&9n&dg&cs"), f("&cR&6a&ei&an&bb&9o&dw &cB&6o&eo&at&bs"));

        // rainbow-armor already exists and is loaded by the config, but there are not any actions added.
        rainbowArmor.setEquipAction(ac -> {
                    Player p = ac.getNetworkPlayer().getPlayer();

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
                        meta.setLore(List.of(f(MessageUtils.forceColor(ac.getLore())).split("\n")));
                    }

                    helmetMeta.setDisplayName(rainbowDisplayNames.get(0)); // In practice, we will not allow players to remove their armor when applied as a cosmetic. (update)
                    chestMeta.setDisplayName(rainbowDisplayNames.get(1));
                    legsMeta.setDisplayName(rainbowDisplayNames.get(2));
                    bootsMeta.setDisplayName(rainbowDisplayNames.get(3));

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
        });

        rainbowArmor.setUnequipAction(ac -> {
            Player p = ac.getNetworkPlayer().getPlayer();

            p.getInventory().setHelmet(null);
            p.getInventory().setChestplate(null);
            p.getInventory().setLeggings(null);
            p.getInventory().setBoots(null);

                    // The class cancels the original tasks for us, provided we add it to the list. This should be all we need
        });

        // Adding some functionalities to prevent duping
        rainbowArmor.on(PlayerDropItemEvent.class, ((event, ac) -> {
            ItemStack itemStack = event.getItemDrop().getItemStack();

            ItemMeta meta = itemStack.getItemMeta();

            if (meta != null && rainbowDisplayNames.contains(meta.getDisplayName())) {
                event.setCancelled(true);
            }
        }));

        rainbowArmor.on(InventoryClickEvent.class, ((event, ac) -> {
            ItemStack itemStack = event.getCurrentItem();

            ItemMeta meta = null;

            if (itemStack != null)
                meta = itemStack.getItemMeta();

            if (meta != null && rainbowDisplayNames.contains(meta.getDisplayName())) {
                event.setCancelled(true);
            }
        }));

        // With this, our actions are registered and will be called by NetworkPlayer.equipCosmetic(Cosmetic cosmetic); when prompted (by command or server join)
        // For simple cosmetics (chat tag, chat color, rank nameplate), we simply won't include them in this driver, we won't set actions.
        // The simple equip/unequip logic doesn't even touch the consumers.


        // ------------------------------------------------------- GRAPPLE-ROD -------------------------------------------------------

        Cosmetic grappleRod = Cosmetic.getCosmetic("grapple-rod");

        if (grappleRod == null) {
            Logger.severe("ActiveCosmetics could not be loaded because there was an exception in the startup (ActiveCosmeticLoader.load()).");
            throw new InvalidConfigException("cosmetics.yml", "cosmetics", "could not find expected cosmetic 'grapple-rod'");
        }

        grappleRod.setEquipAction(ac -> {


            Player p = ac.getNetworkPlayer().getPlayer();

            // remove any existing rod
            for (int i = 0; i < p.getInventory().getSize(); i++) {
                ItemStack is = p.getInventory().getItem(i);

                if (is != null && is.getItemMeta() != null && is.getItemMeta().getDisplayName().equalsIgnoreCase(f(ac.getDisplay()))) {
                    p.getInventory().setItem(i, null);
                }
            }

            ItemStack rod = new ItemStack(Material.FISHING_ROD, 1);

            ItemMeta rodMeta = rod.getItemMeta();

            if (rodMeta != null) {
                rodMeta.setDisplayName(f(ac.getDisplay()));
                rodMeta.setLore(List.of(f(MessageUtils.forceColor(ac.getLore())).split("\n")));
            }

            rod.setItemMeta(rodMeta);

            p.getInventory().addItem(rod);
        });

        grappleRod.setUnequipAction(ac -> {
            Player p = ac.getNetworkPlayer().getPlayer();

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

            Player p = event.getPlayer();

            Vector pull = event.getHook()
                    .getLocation()
                    .toVector()
                    .subtract(p.getLocation().toVector())
                    .normalize()
                    .multiply(2);

            p.setVelocity(pull);

        }));

        // -------------------------------------------------------------- love movement trail --------------------------------------------------------------------------
        Cosmetic loveTrail = Cosmetic.getCosmetic("love-trail");

        if (loveTrail == null) {
            Logger.severe("ActiveCosmetics could not be loaded because there was an exception in the startup (ActiveCosmeticLoader.load()).");
            throw new InvalidConfigException("cosmetics.yml", "cosmetics", "could not find expected cosmetic 'love-trail'");
        }


        loveTrail.setEquipAction(ac -> {

            Player p = ac.getNetworkPlayer().getPlayer();

            BukkitTask task = new BukkitRunnable() {

                @Override
                public void run() {

                    if (p.isOnline())
                        p.getWorld().spawnParticle(Particle.HEART, p.getLocation().add(random.nextDouble(-0.5, 0.5), 1.8 + random.nextDouble(-0.3, 0.3), random.nextDouble(-0.5, 0.5)), 1);

                }
            }.runTaskTimer(plugin, 0L, 10L);

            ac.addTask(task);
            taskIDs.add(task.getTaskId());

        });

        loveTrail.setUnequipAction(ac -> {
            ac.getTasks().forEach(BukkitTask::cancel);
        });

        //   music-trail:
        //    display: "&6Music trail"
        //    lore: "&eUse the power of music to shoot your enemies"
        //    type: PROJECTILE_TRAIL
        //    enabled: true
        //    price: 0
        //    level-requirement: 0
        //    rank-requirement: default
        //
        //  lightning:
        //    display: "&e"
        //    lore: "&eStrike down your opponents with the power of Zeus!"
        //    type: KILL_EFFECT
        //    enabled: true
        //    price: 0
        //    level-requirement: 0
        //    rank-requirement: default

        // ------------------------------------------- Music projectile trail ---------------------------------------------

        Cosmetic musicTrail = Cosmetic.getCosmetic("music-trail");

        if (musicTrail == null) {
            Logger.severe("ActiveCosmetics could not be loaded because there was an exception in the startup (ActiveCosmeticLoader.load()).");
            throw new InvalidConfigException("cosmetics.yml", "cosmetics", "could not find expected cosmetic 'grapple-rod'");
        }

        musicTrail.setUnequipAction(ac -> {
            ac.getTasks().forEach(BukkitTask::cancel);
        });

        musicTrail.on(ProjectileLaunchEvent.class, ((event, ac) -> {

            Logger.debugCosmetic("musicTrail.(ProjectileLaunchEvent.class)");

            Projectile projectile = event.getEntity();

            ProjectileSource source = projectile.getShooter();

            if (!(source instanceof Player)) {
                Logger.debugCosmetic("not instance of player");
                return;
            }

            Player launcher = (Player) source;

            if (!launcher.getUniqueId().equals(ac.getNetworkPlayer().getUUID())) {
                Logger.debugCosmetic(launcher.getUniqueId() + "!=" + ac.getNetworkPlayer().getUUID());
                return;
            }

            NotePitch[] pitches = {NotePitch.F, NotePitch.A, NotePitch.C, NotePitch.E};
            final int[] colors = {7, 4, 2, 10};
            final int[] counter = {0};

            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (this.isCancelled() || projectile.getVelocity().isZero() || projectile.isDead()) {
                        Logger.debugCosmetic("cancelled");
                        this.cancel();
                        return;
                    }

                    Logger.debugCosmetic(projectile.toString());

                    if (counter[0] > 3) {
                        counter[0] = 0;
                    }

                    Location location = projectile.getLocation();
                    float pitch = pitches[counter[0]].pitch;
                    int color = colors[counter[0]];

                    Logger.debugCosmetic("spawned at " + location + String.format(" with pitch=%f [i=%d], color=%d [i=%d]", pitch, counter[0], color, counter[0]));
                    projectile.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, 0.5F, pitch);
                    projectile.getWorld().spawnParticle(Particle.NOTE, location, 1);

                    counter[0]++;

                }
            }.runTaskTimer(plugin, 0L, 1L);

            ac.addTask(task);
            taskIDs.add(task.getTaskId());

        }));


    }
}

