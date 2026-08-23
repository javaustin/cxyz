package com.carrotguy69.cxyz.events.custom;

import com.carrotguy69.cxyz.events.custom.base.Event;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;

public class LevelUpEvent implements Event {

    private final NetworkPlayer player;
    private final int previousLevel;
    private final int newLevel;

    /**
     * Creates a level-up event for the given player.
     *
     * @param player the level-up player
     * @param previousLevel the player's level before the event
     * @param newLevel the player's level after the event (currently)
     *
     */
    public LevelUpEvent(NetworkPlayer player, int previousLevel, int newLevel) {
        this.player = player;
        this.previousLevel = previousLevel;
        this.newLevel = newLevel;
    }

    /**
     * Returns the player who leveled-up.
     *
     * @return the player
     */
    public NetworkPlayer getPlayer() {
        return this.player;
    }

    /**
     * Returns the current level of the player.
     *
     * @return the level
     */
    public int getNewLevel() {
        return this.newLevel;
    }

    /**
     * Returns the previous level of the player.
     *
     * @return the level
     */
    public int getPreviousLevel() {
        return this.previousLevel;
    }

}
