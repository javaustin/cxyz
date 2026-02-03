package com.carrotguy69.cxyz.other;

import com.carrotguy69.cxyz.models.config.GameServer;
import com.carrotguy69.cxyz.models.config.cosmetics.ActiveCosmetic;
import com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic;
import com.carrotguy69.cxyz.models.config.PlayerRank;
import com.carrotguy69.cxyz.models.config.channel.channelTypes.CoreChannel;
import com.carrotguy69.cxyz.models.config.channel.channelTypes.CustomChannel;
import com.carrotguy69.cxyz.models.config.channel.utils.ChannelRegistry;
import com.carrotguy69.cxyz.models.config.shorthand.Shorthand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.stream.Collectors;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Constants {

    public static String getGameServerIP(GameServer gs) {
        FileConfiguration fc = plugin.getConfig();

        return (String) fc.get("servers." + gs.getName());
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
        apiEndpoint = (String) yaml.get("api-endpoint");
        apiKey = (String) yaml.get("api-key");
        apiTimeoutMillis = yaml.getInt("api-timeout", 3000);
        webhook_endpoint = (String) yaml.get("webhook-endpoint");
        servers = GameServer.loadServers();


        for (String s : yaml.getStringList("debugger.enabled-values")) {
            enabledDebugs.add(s.toUpperCase());
        }

        this_server = GameServer.getServerFromName((String) yaml.get("this-server"));
        this_port = yaml.getInt("this-port");
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


        enabledCosmeticTypes = cosmeticsYML.getStringList("enabled-types").stream().map(Cosmetic.CosmeticType::valueOf).collect(Collectors.toList());
        cosmetics = Cosmetic.getCosmetics();
        ActiveCosmetic.loadActiveCosmetics(); // Loading so Cosmetics can use this supplementary class.

        channels.addAll(CustomChannel.loadCustomChannels());
        channels.addAll(CoreChannel.loadCoreChannels());

        ChannelRegistry.loadAssociations();

        shorthandCommands = Shorthand.getShorthandCommands();
    }
}