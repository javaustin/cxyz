package com.carrotguy69.cxyz.other;

import com.carrotguy69.cxyz.exceptions.InvalidConfigException;
import com.carrotguy69.cxyz.models.config.ChatFilterRule;
import com.carrotguy69.cxyz.models.config.services.GameServer;
import com.carrotguy69.cxyz.models.config.cosmetics.ActiveCosmetic;
import com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic;
import com.carrotguy69.cxyz.models.config.PlayerRank;
import com.carrotguy69.cxyz.models.config.channel.channelTypes.CoreChannel;
import com.carrotguy69.cxyz.models.config.channel.channelTypes.CustomChannel;
import com.carrotguy69.cxyz.models.config.channel.utils.ChannelRegistry;
import com.carrotguy69.cxyz.models.config.shorthand.Shorthand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.stream.Collectors;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Constants {

    public static String getGameServerIP(GameServer gs) {
        FileConfiguration fc = plugin.getConfig();

        return (String) fc.get("servers." + gs.getIdentifier());
    }

    public static void loadConfigYMLs() {

        // messages.yml
        File dataFolder = plugin.getDataFolder();

        File msgYMLFile = new File(dataFolder, "messages.yml");

        if (!msgYMLFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        msgYML = YamlConfiguration.loadConfiguration(msgYMLFile);

        // cosmetics.yml
        File file = new File(plugin.getDataFolder(), "cosmetics.yml");

        if (!file.exists()) {
            plugin.saveResource("cosmetics.yml", false);
        }

        cosmeticsYML = YamlConfiguration.loadConfiguration(file);
    }


    public static void loadConstants() {
        loadConfigYMLs();

        FileConfiguration yaml = plugin.getConfig();

        configYaml = yaml;

        apiIdentifier = (String) yaml.get("api.identifier");
        apiEndpoint = (String) yaml.get("api.endpoint");
        apiSecret = (String) yaml.get("api.secret");
        apiTimeoutMillis = yaml.getInt("api.timeout-millis", 3000);

        servers = GameServer.loadServers();

        for (String s : yaml.getStringList("debugger.enabled-values")) {
            enabledDebugs.add(s.toUpperCase());
        }

        thisServer = GameServer.getServerFromName((String) yaml.get("this-server"));
        thisPort = yaml.getInt("this-port");
        timezone = yaml.getString("timezone");
        partyInvitesExpireAfter = yaml.getInt("parties.invites-expire-after", 60);
        partyMaxSize = Math.max(yaml.getInt("parties.max-players", 9999), 1);
        partyAutoKickAfter = yaml.getInt("parties.autokick-after", 60);
        friendRequestsExpireAfter = yaml.getInt("friends.requests-expire-after", 300);
        dateTimeFormat = yaml.getString("datetime-format");
        dateTimeShortFormat = yaml.getString("datetime-short-format");
        permanentString = yaml.getString("punishments.defaults.durations.permanent");
        muteRestrictions = yaml.getStringList("punishments.mute-restrictions");
        ranks = PlayerRank.getRanks();

        chatFilterEnabled = yaml.getBoolean("chat.chat-filter.enabled", false);
        partiesEnabled = yaml.getBoolean("parties.enabled", false);


        ConfigurationSection colors = yaml.getConfigurationSection("colors");

        if (colors != null) {
            for (String key : colors.getKeys(false)) {
                String value = colors.getString(key);

                if (value == null) {
                    continue;
                }

                colorMap.put(key.toUpperCase(), value.toLowerCase());
            }
        }


        enabledCosmeticTypes = cosmeticsYML.getStringList("enabled-types").stream().map(Cosmetic.CosmeticType::valueOf).collect(Collectors.toList());
        cosmetics = Cosmetic.getCosmetics();
        try {
            ActiveCosmetic.loadActiveCosmetics();
        } // Loading so Cosmetics can use this supplementary class.
        catch (InvalidConfigException ex) {
            Logger.logStackTrace(ex);
            Logger.warning("Since an exception was thrown in the ActiveCosmetic loader, some cosmetics may not have loaded properly.");
        }

        channels.addAll(CustomChannel.getCustomChannels());
        channels.addAll(CoreChannel.getCoreChannels());

        chatFilterRules.addAll(ChatFilterRule.getChatFilterRules());

        ChannelRegistry.loadAssociations();

        shorthandCommands = Shorthand.getShorthandCommands();
    }
}