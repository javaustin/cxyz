package com.carrotguy69.cxyz.messages.utils;

import com.carrotguy69.cxyz.messages.MessageKey;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.*;
import static com.carrotguy69.cxyz.messages.MessageUtils.formatPlaceholders;

public class MessageGrabber {

    public static String grab(String key, Map<String, Object> values) {
        String template = msgYML.getString(key, key);

        return formatPlaceholders(template, values); // previously wrapped in f(), but i think this was causing problems with the forceColor function in the parser
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
