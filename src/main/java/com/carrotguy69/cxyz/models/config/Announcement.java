package com.carrotguy69.cxyz.models.config;

import com.carrotguy69.cxyz.exceptions.InvalidConfigException;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.carrotguy69.cxyz.CXYZ.configYaml;

public class Announcement {
    private final String name;
    private final String content;
    private final long delay;
    private final long interval;
    private final List<String> servers;
    private final boolean toConsole;

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

    public List<String> getServers() {
        return servers;
    }

    public boolean sendsToConsole() {
        return toConsole;
    }

    public Announcement (String name, String content, long delay, long interval, List<String> servers, boolean toConsole) {
        this.name = name;
        this.content = content;
        this.delay = delay;
        this.interval = interval;

        List<String> results = new ArrayList<>();
        for (String server : servers) {
            results.add(server.toUpperCase());
        }

        this.servers = results; // Normalizing the server names to uppercase so we can compare it easier later.
        this.toConsole = toConsole;
    }




    public static List<Announcement> getAnnouncements() {
        ConfigurationSection section = configYaml.getConfigurationSection("announcements");

        if (section == null) {
            throw new InvalidConfigException("config.yml", "announcements", "No announcements found!");
        }

        List<Announcement> results = new ArrayList<>();

        for (String name : section.getKeys(false)) {
            if (!Objects.equals(configYaml.getBoolean("announcements." + name + ".enabled"), true))
                continue;

            String content = configYaml.getString("announcements." + name + ".content");
            long delay = configYaml.getLong("announcements." + name + ".delay");
            long interval = configYaml.getLong("announcements." + name + ".interval");
            List<String> servers = configYaml.getStringList("announcements." + name + ".servers");
            boolean toConsole = configYaml.getBoolean("announcements." + name + ".to-console");

            results.add(new Announcement(name, content, delay * 20, interval * 20, servers, toConsole));
        }

        return results;
    }

    public static Announcement getByName(String name) {
        for (Announcement annc : getAnnouncements()) {
            if (annc.getName().equalsIgnoreCase(name)) {
                return annc;
            }
        }

        return null;
    }

    public String toString() {
        return "Announcement{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", delay='" + delay + '\'' +
                ", interval=" + interval +
                ", servers=" + servers +
                ", toConsole=" + toConsole +
                '}';

    }

}
