package com.carrotguy69.cxyz.events.custom.service;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.events.custom.JoinEvent;
import com.carrotguy69.cxyz.events.custom.PublicChatEvent;
import com.carrotguy69.cxyz.events.custom.base.Event;
import com.carrotguy69.cxyz.events.custom.base.EventHandler;
import com.carrotguy69.cxyz.events.custom.base.Priority;
import com.carrotguy69.cxyz.models.config.channel.coreChannels.PublicChannel;
import com.carrotguy69.cxyz.models.config.channel.registry.ChannelFunction;
import com.carrotguy69.cxyz.models.config.channel.registry.ChannelRegistry;

import javax.annotation.Nullable;
import java.util.Objects;

public class EventService {

    public static void dispatch(Event e) {
        if (e instanceof PublicChatEvent) {
            PublicChatEvent pce = (PublicChatEvent) e;

            boolean isHandled = false;
            for (EventHandler<PublicChatEvent> pceHandler : CXYZ.pceHandlers) {
                if (pceHandler.handle(pce)) {
                    isHandled = true;
                }
            }

            if (!isHandled) {
                // Use CXYZ logic as fallback
                PublicChannel channel = Objects.requireNonNull(
                        (PublicChannel) ChannelRegistry.getChannelByFunction(ChannelFunction.PUBLIC)
                );
                channel.handlePublicChatFallback(pce.getSender(), pce.getContent());
            }
        }

        if (e instanceof JoinEvent) {
            JoinEvent je = (JoinEvent) e;

            boolean isHandled = false;
            for (EventHandler<JoinEvent> jeHandler : CXYZ.jeHandlers) {
                if (jeHandler.handle(je)) {
                    isHandled = true;
                }
            }

            // cxyz logic runs regardless of handled or not
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Event> void addEventHandler(Class<T> eventClass, EventHandler<T> handler, @Nullable Priority priority) {
        if (eventClass == PublicChatEvent.class) {
            CXYZ.pceHandlers.add((EventHandler<PublicChatEvent>) handler);
        }
    }
}
