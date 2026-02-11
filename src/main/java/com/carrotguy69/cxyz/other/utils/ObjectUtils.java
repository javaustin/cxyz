package com.carrotguy69.cxyz.other.utils;

import com.google.common.collect.Multimap;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;
import java.util.Objects;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.HashMap;

import java.util.stream.Collectors;

public class ObjectUtils {

    public static boolean containsIgnoreCase(Collection<String> collection, String sequence) {
        for (String s : collection) {
            if (s.toLowerCase().contains(sequence.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public static boolean equalsIgnoreCaseNullSafe(String a, String b) {
        if (a == null && b == null)
            return true;

        if (a == null) {
            return false;
        }

        if (b == null) {
            return false;
        }

        return a.equalsIgnoreCase(b);
    }

    public static int occurrencesOf(String string, String sequence) {
        return string.length() - string.replace(sequence, "").length() / sequence.length();
    }

    public static String[] slice(String[] array, int start, int end) {
        return Arrays.copyOfRange(array, Math.max(0, Math.min(start, array.length)), Math.max(0, Math.min(end, array.length)));
    }

    public static String[] slice(String[] array, int start) {
        return Arrays.copyOfRange(array, Math.max(0, Math.min(start, array.length)), array.length);
    }

    public static String slice_(String[] array, int start) {
        String[] new_array = Arrays.copyOfRange(array, Math.max(0, Math.min(start, array.length)), array.length);
        return String.join(" ", new_array);
    }

    public static String[] removeItem(String[] array, String item) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(array));
        list.remove(item);
        array = new String[list.size()];

        return list.toArray(array);
    }

    public static String formatPlaceHolders(String template, Map<String, Object> values) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String value = entry.getValue() != null ? String.valueOf(entry.getValue()) : "";

            template = template.replace("{" + entry.getKey() + "}", value);
        }

        return ChatColor.translateAlternateColorCodes('&', template);
    }

    public static <K, V> boolean removeFromMultimap(Multimap<K, V> map, K key, V value) {

        if (!map.containsKey(key))
            return false;

        if (!map.containsValue(value))
            return false;

        Collection<V> collection = map.get(key);

        for (V v : collection) {
            if (Objects.equals(value, v)) {
                // Remove the reference
                map.remove(key, v);
            }
        }

        return true;
    }

    public static <K extends Comparable<K>, V> LinkedHashMap<K, V> sortByKey(Map<K, V> map, boolean ascending) {
        return map.entrySet()
                .stream()
                .sorted(ascending ? Map.Entry.comparingByKey() : Map.Entry.<K, V>comparingByKey().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldVal, newVal) -> oldVal,
                        LinkedHashMap::new
                ));
    }

    public static <K, V extends Comparable<V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map, boolean ascending) {
        return map.entrySet()
                .stream()
                .sorted(ascending ? Map.Entry.comparingByValue() : Map.Entry.<K, V>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldVal, newVal) -> oldVal,
                        LinkedHashMap::new
                ));
    }

    public static Map<String, String> invertMap(Map<String, String> map) {
        Map<String, String> reversedMap = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            reversedMap.put(entry.getValue(), entry.getKey());
        }

        return reversedMap;
    }

}
