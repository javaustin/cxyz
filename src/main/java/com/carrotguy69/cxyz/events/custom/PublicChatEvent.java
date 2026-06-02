package com.carrotguy69.cxyz.events.custom;

import com.carrotguy69.cxyz.events.custom.base.Event;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;

public class PublicChatEvent implements Event {

    private final NetworkPlayer sender;
    private String content;

    public PublicChatEvent(NetworkPlayer sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String s) {
        this.content = s;
    }

    public NetworkPlayer getSender() {
        return this.sender;
    }

}