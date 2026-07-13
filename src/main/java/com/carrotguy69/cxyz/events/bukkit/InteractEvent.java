package com.carrotguy69.cxyz.events.bukkit;

import com.carrotguy69.cxyz.cmd.PowerTool;
import com.carrotguy69.cxyz.models.config.cosmetics.ActiveCosmetic;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class InteractEvent {
    public static void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        List<PowerTool.PowerToolEntry> entries = PowerTool.PowerToolEntry.getPlayerPowerTools(p.getUniqueId());
        PowerTool.PowerToolEntry.Type clickType = e.getAction().name().contains("LEFT") ? PowerTool.PowerToolEntry.Type.LEFT_CLICK : PowerTool.PowerToolEntry.Type.RIGHT_CLICK;

        for (PowerTool.PowerToolEntry entry : entries) {
            if (e.getItem() != null && e.getItem().getType() == entry.itemStack.getType()) {
                entry.handleClick(clickType);
            }
        }

        List<ActiveCosmetic> activeCosmetics = ActiveCosmetic.activeCosmeticMap.get(p.getUniqueId());

        if (activeCosmetics == null) {
            return;
        }

        for (ActiveCosmetic ac : activeCosmetics) {
            ac.handleEvent(e);
        }
    }
}
