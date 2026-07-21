package com.carrotguy69.cxyz.events.custom;

import com.carrotguy69.cxyz.events.custom.base.Event;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;

/**
 * Fired when a network player leaves the server.
 */
public class LeaveEvent implements Event {

    private final NetworkPlayer player;

    /**
     * Creates a leave event for the given player.
     *
     * @param player the leaving player
     */
    public LeaveEvent(NetworkPlayer player) {
        this.player = player;
    }

    /**
     * Returns the player who left.
     *
     * @return the leaving player
     */
    public NetworkPlayer getPlayer() {
        return this.player;
    }

}
