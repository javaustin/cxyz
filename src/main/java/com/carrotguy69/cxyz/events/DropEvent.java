package com.carrotguy69.cxyz.events;

import com.carrotguy69.cxyz.models.config.cosmetics.ActiveCosmetic;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.List;

public class DropEvent {

    public static void onDrop(PlayerDropItemEvent event) {
        List<ActiveCosmetic> activeCosmetics = ActiveCosmetic.activeCosmeticMap.get(event.getPlayer().getUniqueId());

        for (ActiveCosmetic ac : activeCosmetics) {
            ac.handleEvent(event);
        }
    }
}
