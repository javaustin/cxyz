package com.carrotguy69.cxyz.other.messages;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.*;
import static com.carrotguy69.cxyz.other.messages.MessageUtils.formatPlaceholders;

public class MessageGrabber {

    public static String grab(String key, Map<String, Object> values) {
        String template = msgYML.getString(key, key);

        return f(formatPlaceholders(template, values));
    }

    public static String grab(MessageKey key, Map<String, Object> values) {
        return grab(key.getPath(), values);
    }

    public static String grab(MessageKey key) {
        return grab(key.getPath(), Map.of());
    }

    public static String grab(String key) {
        return grab(key, Map.of());
    }

}
