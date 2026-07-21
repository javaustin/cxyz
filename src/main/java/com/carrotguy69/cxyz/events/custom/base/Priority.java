package com.carrotguy69.cxyz.events.custom.base;

/**
 * Registration priority for custom handlers.
 * <p>
 * The enum documents intended handler ordering, even though the current
 * service implementation stores handlers in registration order.
 */
public enum Priority {
    LOWEST,
    LOW,
    NORMAL,
    HIGH,
    HIGHEST
}
