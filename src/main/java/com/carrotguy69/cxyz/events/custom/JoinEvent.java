package com.carrotguy69.cxyz.events.custom;

import com.carrotguy69.cxyz.events.custom.base.Event;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;

public class JoinEvent implements Event {

    private final NetworkPlayer player;

    public JoinEvent(NetworkPlayer player) {
        this.player = player;
    }

    public NetworkPlayer getPlayer() {
        return this.player;
    }

}
