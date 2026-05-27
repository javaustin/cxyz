package com.carrotguy69.cxyz.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class BroadcastUtils {
    public static void sendTitle(List<Player> players, String title, String subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        for (Player p : players) {
            p.sendTitle(
                    title,
                    subtitle,
                    fadeInTicks,
                    stayTicks,
                    fadeOutTicks
            );
        }
    }

    public static void playSound(List<Player> players, Sound sound, float volume, float pitch) {
        for (Player p : players) {
            p.playSound(p, sound, volume, pitch);
        }
    }
}
