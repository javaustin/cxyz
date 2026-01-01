package com.carrotguy69.cxyz.other.messages;

import com.carrotguy69.cxyz.other.SimpleTextComponent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static com.carrotguy69.cxyz.CXYZ.*;

public class MessageUtils {

    public static void sendTitleToWorld(World w, String title, String subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        for (Player p : w.getPlayers()) {
            p.sendTitle(f(title), f(subtitle), fadeInTicks, stayTicks, fadeOutTicks);
        }
    }

    public static void playSoundToWorld(World w, Sound sound, float volume, float pitch) {
        for (Player p : w.getPlayers()) {
            p.playSound(p.getLocation(), sound, volume, pitch);
        }
    }

    public static void sendActionBar(Player p, String s) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(f(s)));
    }


    public static String forceColor(String message) {
        // Does not accept "§" characters. Only  translate alternate color codes after we've forced the color through "&" prefixes.

        List<String> colorCodes = List.of("a", "b", "c", "d", "e", "f", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
        List<String> formatCodes = List.of("l", "n", "o", "m", "k");


        StringBuilder output = new StringBuilder();
        String lastColor = "";

        for (int i = 0; i < message.length() - 1; i++) {

            char c0 = message.charAt(i);
            char c1 = message.charAt(i + 1);


            if (c0 == '&') {

                // If is reset code, reset the color. (Resets all previous values)
                if (c1 == 'r') {
                    lastColor = "&r";
                    output.append("&r");

                    i++;
                    continue;
                }

                // If is RGB code, get RGB colors and then add. Skip 13 characters. (Resets all previous values)
                else if (c1 == 'x' && i + 13 < message.length()) {
                    lastColor = message.substring(i, i + 14);
                    output.append(lastColor);

                    i += 13;
                    continue;
                }


                // If this is a legacy COLOR code (not format), this (resets all previous values)
                else if (colorCodes.contains(String.valueOf(c1))) {
                    lastColor = "&" + c1;
                    output.append(lastColor);

                    i++;
                    continue;
                }

                // if this is a legacy FORMAT code (not color), this adds on to the previous color (if exists).
                else if (formatCodes.contains(String.valueOf(c1))) {
                    lastColor = lastColor + "&" + c1;
                    output.append(lastColor);

                    i++;
                    continue;
                }

                // If the following value is none of these i.e -> "&w" (not a real color code), do not modify last color, add literals to output.
                else {
                    output.append(String.valueOf(c0))
                            .append(String.valueOf(c1)); // char to String adapters are necessary or else the chars don't parse correctly

                    i++;
                    continue;
                }


            }

            // If the character is a space -> reapply the last color code
            else if (c0 == ' ') {
                output.append(' ').append(lastColor);
            }

            else {
                output.append(c0);
            }

        }

        // Recover last character (because we use a loop that excludes it for safety)
        output.append(message.charAt(message.length() - 1));

        return output.toString();

//        return message;
    }


    public static String formatPlaceholders(String text, Map<String, Object> placeholders) {
        for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
            String value = entry.getValue() != null ? String.valueOf(entry.getValue()) : "";

            text = text.replace("{" + entry.getKey() + "}", value);
        }

        return text;
    }

    public static void sendUnparsedMessage(Player p, String content, Map<String, Object> formatMap) {
        if (!p.isOnline()) {
            return;
        }

        p.sendMessage(f(formatPlaceholders(content, formatMap)));
    }

    public static void sendParsedMessage(Player p, MessageKey key, Map<String, Object> map) {
        if (!p.isOnline()) {
            return;
        }

        String template = MessageGrabber.grab(key);

        MessageParser parser = new MessageParser(template, map);
        List<SimpleTextComponent> components = parser.safeParse();

        p.spigot().sendMessage(parser.toTextComponent(components));
    }


    public static void sendParsedMessage(Player p, String unparsedContent, Map<String, Object> map) {
        if (!p.isOnline()) {
            return;
        }

        MessageParser parser = new MessageParser(unparsedContent, map);
        List<SimpleTextComponent> components = parser.safeParse();

        p.spigot().sendMessage(parser.toTextComponent(components));
    }

    public static void sendParsedMessage(CommandSender sender, MessageKey key, Map<String, Object> map) {

        String template = MessageGrabber.grab(key);

        MessageParser parser = new MessageParser(template, map);
        List<SimpleTextComponent> components = parser.safeParse();

        sender.spigot().sendMessage(parser.toTextComponent(components));
    }

    public static void sendParsedMessage(CommandSender sender, String unparsedContent, Map<String, Object> map) {

        MessageParser parser = new MessageParser(unparsedContent, map);
        List<SimpleTextComponent> components = parser.safeParse();

        sender.spigot().sendMessage(parser.toTextComponent(components));
    }

    public static void sendPublicMessage(String content, boolean parsed, Map<String, Object> formatMap) {
        // Similar to Bukkit.broadcastMessage(...), but accounts for those who mute the public channel. (EDIT: removed muted channel check, most of our stuff is important enough i reckon)
        // This should be used for mutable announcements, but not for critical game info.

        for (Player p : Bukkit.getOnlinePlayers()) {

            // NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

//            if (np.isMutingChannel("PUBLIC")) {
//                continue;
//            }

            if (!parsed) {
                p.sendMessage(content);
                Bukkit.getConsoleSender().sendMessage(content);
            }

            else {
                MessageUtils.sendParsedMessage(p, content, formatMap);
                MessageUtils.sendParsedMessage(Bukkit.getConsoleSender(), content, formatMap);
            }

        }
    }

}
