package com.carrotguy69.cxyz.events.custom.service;

import com.carrotguy69.cxyz.events.custom.base.Event;
import com.carrotguy69.cxyz.events.custom.base.EventHandler;
import com.carrotguy69.cxyz.events.custom.base.Priority;

import java.util.ArrayList;
import java.util.EnumMap;
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


        private static final Map<Class<? extends Event>, EnumMap<Priority, List<EventHandler<? extends Event>>>> handlers = new HashMap<>();


        @SuppressWarnings("unchecked")
        public static void dispatch(Event e) {
            EnumMap<Priority, List<EventHandler<? extends Event>>> byPriority = handlers.get(e.getClass());
            if (byPriority == null) return;

            // iterate in priority order (lowest -> highest). For highest-first, iterate reverse.
            for (int i = Priority.values().length - 1; i >= 0; i--) {
                Priority p = Priority.values()[i];

                List<EventHandler<? extends Event>> bucket = byPriority.get(p);
                if (bucket == null) continue;

                for (EventHandler<? extends Event> h : bucket) {
                    EventHandler<Event> cast = (EventHandler<Event>) h;
                    if (cast.handle(e)) return;
                }
            }
        }

        @SuppressWarnings("unchecked")
        public static <T extends Event> void registerHandler(Class<T> type, EventHandler<T> handler, Priority priority) {
            EnumMap<Priority, List<EventHandler<? extends Event>>> byPriority =
                    handlers.computeIfAbsent(type, k -> {
                        EnumMap<Priority, List<EventHandler<? extends Event>>> m =
                                new EnumMap<>(Priority.class);
                        for (Priority p : Priority.values()) m.put(p, new ArrayList<>());
                        return m;
                    });

            byPriority.get(priority).add(handler); // insertion order within same priority
        }




}
