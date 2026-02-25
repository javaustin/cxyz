package com.carrotguy69.cxyz.models.config.services;

import com.carrotguy69.cxyz.exceptions.InvalidConfigException;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

import static com.carrotguy69.cxyz.CXYZ.configYaml;
import static com.carrotguy69.cxyz.CXYZ.servers;

public class GameServer extends Service {

    public GameServer(String identifier, String ip, String secret) {
        super(identifier, ip, secret);
    }

    public static GameServer getServerFromName(String name) {
        for (GameServer gs : servers) {
            if (gs.getIdentifier().equals(name)) {
                return gs;
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
            String ip = configYaml.getString("config.servers." + name + ".ip-address");
            String secret = configYaml.getString("config.servers." + name + ".secret");

            result.add(new GameServer(name, ip, secret));
        }

        if (result.isEmpty()) {
            throw new InvalidConfigException("config.yml", "servers", "No game servers are defined!");
        }

        return result;
    }
}
