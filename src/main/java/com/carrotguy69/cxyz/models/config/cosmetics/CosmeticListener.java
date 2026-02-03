package com.carrotguy69.cxyz.models.config.cosmetics;

import org.bukkit.event.Event;

public interface CosmeticListener<T extends Event> {
    void handle(T event, ActiveCosmetic ac);

}
