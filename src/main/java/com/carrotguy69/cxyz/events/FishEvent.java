package com.carrotguy69.cxyz.events;

import com.carrotguy69.cxyz.models.config.cosmetics.ActiveCosmetic;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.List;

public class FishEvent {
    public static void onFish(PlayerFishEvent event) {
        List<ActiveCosmetic> activeCosmetics = ActiveCosmetic.activeCosmeticMap.get(event.getPlayer().getUniqueId());

        for (ActiveCosmetic ac : activeCosmetics) {
            ac.handleEvent(event);
        }
    }
}
