package com.carrotguy69.cxyz.utils;

import org.bukkit.Color;

public class ColorUtils {
    public static int getRGB(String input) {
        // Gets first display color from a string "&aHello world" -> 0x55FF55

        if (input == null || input.isEmpty())
            return -1;

        char[] chars = input.toCharArray();

        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == '&') {
                char code = Character.toLowerCase(chars[i + 1]);

                // RGB FORMAT: &x&f&f&f&f&f&f
                if (code == 'x' && i + 13 < chars.length) {
                    StringBuilder hex = new StringBuilder();

                    for (int j = 0; j < 6; j++) {
                        int index = i + 2 + (j * 2);
                        if (chars[index] == '&') {
                            hex.append(chars[index + 1]);
                        } else {
                            break;
                        }
                    }

                    if (hex.length() == 6) {
                        try {
                            return Integer.parseInt(hex.toString(), 16);
                        }
                        catch (NumberFormatException ignored) {}
                    }
                }

                // Legacy colors
                return getLegacyRGB(code);
            }
        }

        return -1;
    }

    private static int getLegacyRGB(char code) {
        return switch (code) {
            case '0' -> 0x000000; // black
            case '1' -> 0x0000AA; // dark blue
            case '2' -> 0x00AA00; // dark green
            case '3' -> 0x00AAAA; // dark aqua
            case '4' -> 0xAA0000; // dark red
            case '5' -> 0xAA00AA; // dark purple
            case '6' -> 0xFFAA00; // gold
            case '7' -> 0xAAAAAA; // gray
            case '8' -> 0x555555; // dark gray
            case '9' -> 0x5555FF; // blue
            case 'a' -> 0x55FF55; // green
            case 'b' -> 0x55FFFF; // aqua
            case 'c' -> 0xFF5555; // red
            case 'd' -> 0xFF55FF; // light purple
            case 'e' -> 0xFFFF55; // yellow
            case 'f' -> 0xFFFFFF; // white
            default -> -1; // ignore formatting codes
        };
    }

    public static String getColorCode(int rgb) {
        if (rgb < 0) {
            return "";
        }

        switch (rgb) {

            case 0x000000: return "&0"; // black
            case 0x0000AA: return "&1"; // dark blue
            case 0x00AA00: return "&2"; // dark green
            case 0x008080: return "&3"; // dark aqua
            case 0xAA0000: return "&4"; // dark red
            case 0xAA00AA: return "&5"; // dark purple
            case 0xFFAA00: return "&6"; // gold
            case 0xAAAAAA: return "&7"; // gray
            case 0x555555: return "&8"; // dark gray
            case 0x5555FF: return "&9"; // blue
            case 0x55FF55: return "&a"; // green
            case 0x55FFFF: return "&b"; // aqua
            case 0xFF5555: return "&c"; // red
            case 0xFF55FF: return "&d"; // light purple
            case 0xFFFF55: return "&e"; // yellow
            case 0xFFFFFF: return "&f"; // white
            default:
                // Ensure it's 6-digit hex (pad with zeros if needed)
                String hexString = String.format("%06X", rgb);

                StringBuilder result = new StringBuilder("&x");
                for (char c : hexString.toCharArray()) {
                    result.append('&').append(Character.toLowerCase(c));
                }

                return result.toString();
        }
    }

    public static Color getAdaptedArmorColor(int rgb) {

        return switch (rgb) {
            case 0x000000 -> Color.BLACK; // black
            case 0x0000AA -> Color.NAVY; // dark blue
            case 0x00AA00 -> Color.GREEN; // dark green
            case 0x00AAAA -> Color.TEAL; // dark aqua
            case 0xAA0000 -> Color.MAROON; // dark red
            case 0xAA00AA -> Color.PURPLE; // dark purple
            case 0xFFAA00 -> Color.ORANGE; // gold
            case 0xAAAAAA -> Color.SILVER; // gray
            case 0x555555 -> Color.GRAY; // dark gray
            case 0x5555FF -> Color.BLUE; // blue
            case 0x55FF55 -> Color.GREEN; // green
            case 0x55FFFF -> Color.AQUA; // aqua
            case 0xFF5555 -> Color.RED; // red
            case 0xFF55FF -> Color.FUCHSIA; // light purple
            case 0xFFFF55 -> Color.YELLOW; // yellow
            case 0xFFFFFF -> Color.WHITE; // white
            default -> Color.fromRGB(rgb);
        };



    }
}
