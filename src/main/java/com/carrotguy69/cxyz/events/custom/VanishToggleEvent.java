package com.carrotguy69.cxyz.events.custom;

import com.carrotguy69.cxyz.events.custom.base.Event;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;

/**
 * Fired after a player's vanish state is toggled.
 */
public class VanishToggleEvent implements Event {

    private final NetworkPlayer player;
    private final boolean toggle;

    /**
     * Creates a vanish toggle event.
     *
     * @param player the affected player
     * @param toggle {@code true} when vanish is enabled, {@code false} when disabled
     */
    public VanishToggleEvent(NetworkPlayer player, boolean toggle) {
        this.player = player;
        this.toggle = toggle;
    }

    /**
     * Returns the new vanish state.
     *
     * @return {@code true} if vanish is enabled
     */
    public boolean getToggle() {
        return toggle;
    }

    /**
     * Returns the player whose vanish state changed.
     *
     * @return the affected player
     */
    public NetworkPlayer getPlayer() {
        return player;
    }
}
