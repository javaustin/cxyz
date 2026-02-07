package com.carrotguy69.cxyz.events;

import com.carrotguy69.cxyz.models.config.cosmetics.ActiveCosmetic;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class ClickEvent {

    public static void onClick(InventoryClickEvent event) {
        List<ActiveCosmetic> activeCosmetics = ActiveCosmetic.activeCosmeticMap.get(event.getWhoClicked().getUniqueId());

        for (ActiveCosmetic ac : activeCosmetics) {
            ac.handleEvent(event);
        }
    }
}
