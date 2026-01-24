package com.carrotguy69.cxyz.other.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.carrotguy69.cxyz.CXYZ.*;

public class TimeUtils {
    public static long unixTimeNow() {
        return System.currentTimeMillis() / 1000;
    }

    public static String dateOf(long timestamp, String timeZone) {
        if (timestamp == -1) {
            return permanentString;
        }

        Instant instant = Instant.ofEpochSecond(timestamp);

        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(timeZone));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);

        return zonedDateTime.format(formatter);
    }

    public static String dateOfShort(long timestamp, String timeZone) {
        if (timestamp == -1) {
            return permanentString;
        }

        Instant instant = Instant.ofEpochSecond(timestamp);

        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(timeZone));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeShortFormat);

        return zonedDateTime.format(formatter);
    }


    public static String unixCountdownShort(long timestamp) {
        if (timestamp == -1) {
            return permanentString;
        }

        long countDownTime = timestamp - unixTimeNow();

        List<String> formattedTime = new ArrayList<>();

        LinkedHashMap<Long, String> timeUnits = new LinkedHashMap<>();

        timeUnits.put((86400 * 365L), "y");
        timeUnits.put((86400 * 30L), "mo");
//        timeUnits.put((86400 * 7L), "week");
        timeUnits.put(86400L, "d");
        timeUnits.put(3600L, "h");
        timeUnits.put(60L, "m");
        timeUnits.put(1L, "s");

        for (Map.Entry<Long, String> entry : timeUnits.entrySet()) {

            long num = entry.getKey();
            String unit = entry.getValue();

            if (countDownTime < num) {
                continue;
            }

            long units = (long) Math.floor((double) countDownTime / (int) num);

            countDownTime -= (num * units);


            formattedTime.add(units + unit);
        }

        String result = String.join(" ", formattedTime);

        if (result.isBlank()) {
            result = "0 seconds";
        }

        return result;
    }

    public static String unixCountdown(long timestamp) {
        if (timestamp == -1) {
            return permanentString;
        }

        long countDownTime = timestamp - unixTimeNow();

        List<String> formattedTime = new ArrayList<>();

        LinkedHashMap<Long, String> timeUnits = new LinkedHashMap<>();

        timeUnits.put((86400 * 365L), "year");
        timeUnits.put((86400 * 30L), "month");
//        timeUnits.put((86400 * 7L), "week");
        timeUnits.put(86400L, "day");
        timeUnits.put(3600L, "hour");
        timeUnits.put(60L, "minute");
        timeUnits.put(1L, "second");


        for (Map.Entry<Long, String> entry : timeUnits.entrySet()) {

            long num = entry.getKey();
            String unit = entry.getValue();

            if (countDownTime < num) {
                continue;
            }

            long units = (long) Math.floor((double) countDownTime / (int) num);

            countDownTime -= (num * units);

            String s = "s";
            if (units == 1) {
                s = "";
            }

            formattedTime.add(String.format("%s %s%s", units, unit, s));
        }

        String delimiter = formattedTime.size() > 2 ? ", " : " ";

        String result = String.join(delimiter, formattedTime);

        if (result.isBlank()) {
            result = "0 seconds";
        }

        return result;
    }

    public static String getTimezoneShort(String longZoneId) {

        ZoneId zone = ZoneId.of(longZoneId);
        ZonedDateTime now = ZonedDateTime.now(zone);

        return now.format(DateTimeFormatter.ofPattern("z"));
    }

    private static List<String> splitTimeSegments(String timeString) {
        List<String> segments = new ArrayList<>();

        Pattern regex = Pattern.compile("\\d+[smhdwy]");
        Matcher matcher = regex.matcher(timeString);

        while (matcher.find()) {
            segments.add(matcher.group());
        }

        return segments;
    }

    private static long convert(String s) {
        // Convert a single time ("1h") string into the amount of seconds (3600L)

        Map<String, Long> timeUnits = Map.of(
                "s", 1L,
                "m", 60L,
                "h", 3600L,
                "d", 86400L,
                "w", 604800L,
                "y", 31622400L
        );


        try {
            return Long.parseLong( // given "20s"
                    s.substring(0, s.length() - 1)) // [:-1] - gets the number without the letter (20)
                    *
                    timeUnits.get(s.substring(s.length() - 1)); // This warning is lying to you...
        }
        catch (Exception ex) {
            return Long.parseLong(s);
        }
    }

    public static long toSeconds(String s) {
        long total = 0L;
        for (String segment : splitTimeSegments(s)) {
            total += convert(segment);
        }

        if (total == 0L) {
            return convert(s);
        }

        return total;
    }

    public static boolean validTimeString(String s) {
        try {
            toSeconds(s);
        }
        catch (NumberFormatException ex2) {
            return false;
        }
        return true;
    }
}
