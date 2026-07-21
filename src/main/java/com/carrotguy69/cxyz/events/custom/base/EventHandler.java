package com.carrotguy69.cxyz.events.custom.base;

/**
 * Functional contract for custom event listeners.
 *
 * @param <T> the event type handled by this listener
 */
public interface EventHandler<T extends Event> {

    /**
     * Handles a dispatched event.
     *
     * @param event the event instance
     * @return {@code true} to stop further handler processing, or {@code false}
     * to allow later handlers to continue
     */
    boolean handle(T event);
}
