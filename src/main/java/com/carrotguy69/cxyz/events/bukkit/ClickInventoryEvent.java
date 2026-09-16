package com.carrotguy69.cxyz.events.bukkit;

import com.carrotguy69.cxyz.cmd.EnchantTable;
import com.carrotguy69.cxyz.models.config.cosmetics.ActiveCosmetic;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class ClickInventoryEvent {

    public static void onClick(InventoryClickEvent event) {
        List<ActiveCosmetic> activeCosmetics = ActiveCosmetic.activeCosmeticMap.get(event.getWhoClicked().getUniqueId());

        if (activeCosmetics != null) {
            for (ActiveCosmetic ac : activeCosmetics) {
                ac.handleEvent(event);
            }
        }

        if (
                event.getClickedInventory() != null &&
                event.getClickedInventory().getType() == InventoryType.ENCHANTING &&
                EnchantTable.inGUI.contains(event.getWhoClicked().getUniqueId()) &&
                event.getCurrentItem() != null
                && event.getCurrentItem().getType() == Material.LAPIS_LAZULI) {

            event.setCancelled(true);

        }


    }
}
