package com.carrotguy69.cxyz.events.bukkit;

import com.carrotguy69.cxyz.cmd.EnchantTable;
import com.carrotguy69.cxyz.models.config.cosmetics.ActiveCosmetic;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;

public class DragInventoryEvent {

    public static void onDrag(InventoryDragEvent event) {
        List<ActiveCosmetic> activeCosmetics = ActiveCosmetic.activeCosmeticMap.get(event.getWhoClicked().getUniqueId());

        if (activeCosmetics != null) {
            for (ActiveCosmetic ac : activeCosmetics) {
                ac.handleEvent(event);
            }
        }

        if (
                event.getInventory().getType() == InventoryType.ENCHANTING &&
                EnchantTable.inGUI.contains(event.getWhoClicked().getUniqueId()) &&
                event.getCursor() != null
                && event.getCursor().getType() == Material.LAPIS_LAZULI) {

            event.setCancelled(true);

        }


    }
}
