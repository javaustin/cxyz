package com.carrotguy69.cxyz.events.custom.localHandlers;

import com.carrotguy69.cxyz.events.custom.PublicChatEvent;
import com.carrotguy69.cxyz.events.custom.base.EventHandler;
import com.carrotguy69.cxyz.models.config.channel.coreChannels.PublicChannel;
import com.carrotguy69.cxyz.models.config.channel.registry.ChannelFunction;
import com.carrotguy69.cxyz.models.config.channel.registry.ChannelRegistry;

import java.util.Objects;

public class ChatFallbackHandler implements EventHandler<PublicChatEvent> {

    @Override
    public boolean handle(PublicChatEvent e) {
        PublicChannel channel = Objects.requireNonNull(
                (PublicChannel) ChannelRegistry.getChannelByFunction(ChannelFunction.PUBLIC)
        );
        channel.handlePublicChatFallback(e.getSender(), e.getContent());

        return false; // Return false to continue further handling by other registered handlers.
    }
}