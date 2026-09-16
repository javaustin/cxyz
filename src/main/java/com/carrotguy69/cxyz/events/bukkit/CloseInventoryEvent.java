package com.carrotguy69.cxyz.events.bukkit;

import com.carrotguy69.cxyz.cmd.EnchantTable;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class CloseInventoryEvent {

    public static void onClose(InventoryCloseEvent e) {
        EnchantTable.inGUI.remove(e.getPlayer().getUniqueId());
    }
}
