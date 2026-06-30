package com.carrotguy69.cxyz.events.custom;

import com.carrotguy69.cxyz.events.custom.base.Event;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;

public class LeaveEvent implements Event {

    private final NetworkPlayer player;

    public LeaveEvent(NetworkPlayer player) {
        this.player = player;
    }

    public NetworkPlayer getPlayer() {
        return this.player;
    }

}
