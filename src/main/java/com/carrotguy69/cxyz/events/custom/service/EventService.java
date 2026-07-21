package com.carrotguy69.cxyz.events.custom.service;

import com.carrotguy69.cxyz.events.custom.base.Event;
import com.carrotguy69.cxyz.events.custom.base.EventHandler;
import com.carrotguy69.cxyz.events.custom.base.Priority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central dispatcher for the custom event system.
 * <p>
 * Handlers are registered by event type and invoked synchronously when an
 * event is dispatched. Processing stops early when a handler returns
 * {@code true}.
 */
public class EventService {

    private static final Map<Class<? extends Event>, List<EventHandler<? extends Event>>> handlers = new HashMap<>();

    /**
     * Dispatches an event to all handlers registered for the event's runtime type.
     * <p>
     * If no handlers are registered, the method returns immediately.
     *
     * @param e the event to dispatch
     */
    @SuppressWarnings("unchecked")
    public static void dispatch(Event e) {

        List<EventHandler<? extends Event>> typeHandlers = handlers.get(e.getClass());

        if (typeHandlers == null)
            return;

        for (EventHandler<? extends Event> h : typeHandlers) {
            EventHandler<Event> cast = (EventHandler<Event>) h;

            if (cast.handle(e)) {
                return;
            }
        }
    }

    /**
     * Registers a handler for a specific event type.
     * <p>
     *
     * @param type the event class to bind to
     * @param handler the handler to register
     * @param priority the declared priority for the handler
     * @param <T> the event type
     */
    @SuppressWarnings("unchecked")
    public static <T extends Event> void registerHandler(Class<T> type, EventHandler<T> handler, Priority priority) {
        handlers.computeIfAbsent(type, k -> new ArrayList<>()).add(handler);
    }
}
