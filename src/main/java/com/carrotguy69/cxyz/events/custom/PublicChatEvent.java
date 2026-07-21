package com.carrotguy69.cxyz.events.custom;

import com.carrotguy69.cxyz.events.custom.base.Event;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;

/**
 * Fired before a public chat message is handled by the custom chat pipeline.
 * <p>
 * Handlers may inspect or replace the message content before later handlers or
 * the fallback chat routing runs.
 */
public class PublicChatEvent implements Event {

    private final NetworkPlayer sender;
    private String content;

    /**
     * Creates a public chat event.
     *
     * @param sender the player who sent the message
     * @param content the original message content
     */
    public PublicChatEvent(NetworkPlayer sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    /**
     * Returns the current message content.
     *
     * @return the message content
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Replaces the message content.
     *
     * @param s the new content
     */
    public void setContent(String s) {
        this.content = s;
    }

    /**
     * Returns the message sender.
     *
     * @return the sender
     */
    public NetworkPlayer getSender() {
        return this.sender;
    }

}
