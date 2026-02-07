package com.carrotguy69.cxyz.events;

import com.carrotguy69.cxyz.models.config.cosmetics.ActiveCosmetic;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.List;

public class ProjectileEvent {
    public static void onProjectile(ProjectileLaunchEvent event) {

        if (event.getEntity().getShooter() instanceof Player) {
            List<ActiveCosmetic> activeCosmetics = ActiveCosmetic.activeCosmeticMap.get(((Player) event.getEntity().getShooter()).getUniqueId());

            for (ActiveCosmetic ac : activeCosmetics) {
                ac.handleEvent(event);
            }
        }

    }}
