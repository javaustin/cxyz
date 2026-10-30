package com.carrotguy69.cxyz.models.config;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.cmd.Broadcast;
import com.carrotguy69.cxyz.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.other.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.announcements;
import static com.carrotguy69.cxyz.CXYZ.configYaml;

public class Announcement {
    private final String name;
    private final String content;
    private final String channel;
    private final long delay;
    private final long interval;
    private final boolean isGlobal;
    private final boolean isEnabled;

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public long getDelay() {
        return delay;
    }

    public long getInterval() {
        return interval;
    }

    public Announcement (String name, String content, String channel, long delaySeconds, long intervalSeconds, boolean isGlobal, boolean isEnabled) {
        this.name = name;
        this.content = content;
        this.channel = channel;
        this.delay = delaySeconds;
        this.interval = intervalSeconds;
        this.isGlobal = isGlobal;
        this.isEnabled = isEnabled;
    }

    public static List<Announcement> getAnnouncements() {
        ConfigurationSection section = configYaml.getConfigurationSection("announcements");

        if (section == null) {
            Logger.warning("No announcements found in config.yml! Ignoring...");
            return List.of();
        }

        List<Announcement> results = new ArrayList<>();

        for (String name : section.getKeys(false)) {

            String content = configYaml.getString("announcements." + name + ".content");
            String channel = configYaml.getString("announcements." + name + ".channel");
            long delay = configYaml.getLong("announcements." + name + ".delay");
            long interval = configYaml.getLong("announcements." + name + ".interval");
            boolean isGlobal = configYaml.getString("announcements." + name + ".scope", "local").equalsIgnoreCase("GLOBAL");
            boolean isEnabled = configYaml.getBoolean("announcements." + name + ".enabled");

            results.add(new Announcement(name, content, channel, delay, interval, isGlobal, isEnabled));
        }

        return results;
    }

    public void send() {
        new BukkitRunnable() {public void run () {
            BaseChannel baseChannel = BaseChannel.getChannel(channel);

            if (baseChannel == null) {
                Logger.debug("base channel is null, sending broadcast with isGlobal=%b".formatted(isGlobal));
                Broadcast.broadcast(content, Map.of(), isGlobal);
            }
            else {
                Logger.debug("base channel is " + baseChannel.getName());
                baseChannel.sendChannelMessage(content, Map.of(), isGlobal);
            }

        }}.runTask(CXYZ.plugin);
    }

    public static Announcement getByName(String name) {
        for (Announcement announcement : announcements) {
            if (announcement.getName().equalsIgnoreCase(name)) {
                return announcement;
            }
        }

        return null;
    }

    public String toString() {
        return "Announcement{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", channel='" + channel + '\'' +
                ", delay='" + delay + '\'' +
                ", interval=" + interval +
                ", isGlobal=" + isGlobal +
                ", isEnabled=" + isEnabled +
                '}';

    }

    public boolean isEnabled() {
        return isEnabled;
    }

}
