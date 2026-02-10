package com.carrotguy69.cxyz.events;

import com.carrotguy69.cxyz.models.config.cosmetics.ActiveCosmetic;
import com.carrotguy69.cxyz.other.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.List;

public class ProjectileEvent {
    public static void onProjectile(ProjectileLaunchEvent event) {
        Logger.debugCosmetic("event called");
        if (event.getEntity().getShooter() instanceof Player) {
            List<ActiveCosmetic> activeCosmetics = ActiveCosmetic.activeCosmeticMap.get(((Player) event.getEntity().getShooter()).getUniqueId());

            Logger.debugCosmetic("activeCosmetics: " + activeCosmetics);
            for (ActiveCosmetic ac : activeCosmetics) {
                ac.handleEvent(event);
            }
        }

    }}
