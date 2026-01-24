package com.carrotguy69.cxyz.other.utils;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.gson;

public class JsonConverters {

    public static List<Map<String, Object>> toListOfMap(String input) {
        return gson.fromJson(input, new TypeToken<List<Map<String, Object>>>() {}.getType());
    }

    public static Map<String, Object> toMap(String input) {
        return gson.fromJson(input, new TypeToken<Map<String, Object>>() {}.getType());
    }

    public static Map<String, String> toMapString(String input) {
        return gson.fromJson(input, new TypeToken<Map<String, Object>>() {}.getType());
    }

    public static Map.Entry<String, List<String>> toPartyObject (String input) {
        return gson.fromJson(input, new TypeToken<Map.Entry<String, List<String>>>() {}.getType());
    }

    public static List<String> toList(String input) {
        return gson.fromJson(input, new TypeToken<ArrayList<String>>() {}.getType());
    }
}
