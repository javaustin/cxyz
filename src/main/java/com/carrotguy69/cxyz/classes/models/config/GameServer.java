package com.carrotguy69.cxyz.classes.models.config;

import com.carrotguy69.cxyz.classes.exceptions.InvalidConfigException;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

import static com.carrotguy69.cxyz.CXYZ.configYaml;
import static com.carrotguy69.cxyz.CXYZ.servers;

public class GameServer {

    private final String ip;
    private final String name;

    GameServer(String name, String ip) {
        this.name = name;
        this.ip = ip;
    }

    public static GameServer getServerFromName(String name) {
        for (GameServer gs : servers) {
            if (gs.getName().equals(name)) {
                return new GameServer(gs.getName(), gs.getIP());
            }
        }

        return null;
    }

    public static List<GameServer> loadServers() {
        List<GameServer> result = new ArrayList<>();

        ConfigurationSection section = configYaml.getConfigurationSection("servers");

        if (section == null) {
            throw new InvalidConfigException("config.yml", "servers", "No game servers are defined!");
        }

        for (String name : section.getKeys(false)) {
            String ip = configYaml.getString("config.servers." + name);

            result.add(new GameServer(name, ip));
        }

        if (result.isEmpty()) {
            throw new InvalidConfigException("config.yml", "servers", "No game servers are defined!");
        }

        return result;
    }

    public String getIP() {
        return ip;
    }

    public String getName() {
        return name;
    }
}
