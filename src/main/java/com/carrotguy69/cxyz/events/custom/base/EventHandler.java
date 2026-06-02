package com.carrotguy69.cxyz.events.custom.base;

public interface EventHandler<T extends Event> {
    boolean handle(T event);
}