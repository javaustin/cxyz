package com.carrotguy69.cxyz.other;

import com.carrotguy69.cxyz.classes.models.config.GameServer;
import com.carrotguy69.cxyz.classes.models.config.ActiveCosmetic;
import com.carrotguy69.cxyz.classes.models.config.Cosmetic;
import com.carrotguy69.cxyz.classes.models.config.PlayerRank;
import com.carrotguy69.cxyz.classes.models.config.channel.channelTypes.CoreChannel;
import com.carrotguy69.cxyz.classes.models.config.channel.channelTypes.CustomChannel;
import com.carrotguy69.cxyz.classes.models.config.channel.utils.ChannelRegistry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Constants {

    public static String getGameServerIP(GameServer gs) {
        FileConfiguration fc = instance.getConfig();

        return (String) fc.get("servers." + gs.getName());
    }

    public static void initThings() {

        // messages.yml
        File dataFolder = instance.getDataFolder();

        File msgYMLFile = new File(dataFolder, "messages.yml");

        if (!msgYMLFile.exists()) {
            instance.saveResource("messages.yml", false);
        }

        msgYML = YamlConfiguration.loadConfiguration(msgYMLFile);

        // cosmetics.yml
        File file = new File(instance.getDataFolder(), "cosmetics.yml");

        if (!file.exists()) {
            instance.saveResource("cosmetics.yml", false);
        }

        cosmeticsYML = YamlConfiguration.loadConfiguration(file);
    }


    public static void loadConstantsFromYAML() {
        initThings();

        FileConfiguration yaml = instance.getConfig();

        configYaml = yaml;
        discord_url = (String) yaml.get("discord-url");
        server_name = (String) yaml.get(f("server-name"));
        server_ip = (String) yaml.get(f("server-ip"));
        api_endpoint = (String) yaml.get("api-endpoint");
        api_timeout = yaml.getInt("api-timeout", 3000);
        webhook_endpoint = (String) yaml.get("webhook-endpoint");
        servers = GameServer.loadServers();
        this_server = GameServer.getServerFromName((String) yaml.get("this-server"));
        this_port = yaml.getInt("this-port");
        timezone = yaml.getString("timezone");
        partyInvitesExpireAfter = yaml.getInt("parties.invites-expire-after", 60);
        friendRequestsExpireAfter = yaml.getInt("friends.requests-expire-after", 300);
        dateTimeFormat = yaml.getString("datetime-format");
        dateTimeShortFormat = yaml.getString("datetime-short-format");
        permanentString = yaml.getString("punishments.defaults.durations.permanent");
        muteRestrictions = yaml.getStringList("punishments.mute-restrictions");
        ranks = PlayerRank.loadRanks();

        cosmetics = Cosmetic.loadCosmetics();
        ActiveCosmetic.loadActiveCosmetics(); // Loading so Cosmetics can use this supplementary class.

        channels.addAll(CustomChannel.loadCustomChannels());
        channels.addAll(CoreChannel.loadCoreChannels());

        ChannelRegistry.loadAssociations();
    }
}