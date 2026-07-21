package com.carrotguy69.cxyz.events.custom;

import com.carrotguy69.cxyz.events.custom.base.Event;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;

/**
 * Fired when a network player joins the server.
 */
public class JoinEvent implements Event {

    private final NetworkPlayer player;

    /**
     * Creates a join event for the given player.
     *
     * @param player the joining player
     */
    public JoinEvent(NetworkPlayer player) {
        this.player = player;
    }

    /**
     * Returns the player who joined.
     *
     * @return the joining player
     */
    public NetworkPlayer getPlayer() {
        return this.player;
    }

}
