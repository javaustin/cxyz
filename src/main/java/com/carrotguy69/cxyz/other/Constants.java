package com.carrotguy69.cxyz.other;

import com.carrotguy69.cxyz.exceptions.InvalidConfigException;
import com.carrotguy69.cxyz.models.config.ChatFilterRule;
import com.carrotguy69.cxyz.models.config.cosmetics.ActiveCosmetic;
import com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic;
import com.carrotguy69.cxyz.models.config.PlayerRank;
import com.carrotguy69.cxyz.models.config.channel.channelTypes.CoreChannel;
import com.carrotguy69.cxyz.models.config.channel.channelTypes.CustomChannel;
import com.carrotguy69.cxyz.models.config.channel.registry.ChannelRegistry;
import com.carrotguy69.cxyz.models.config.shorthand.Shorthand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Constants {


    public static void loadConfigYMLs() {

        // messages.yml
        File dataFolder = plugin.getDataFolder();

        File msgYMLFile = new File(dataFolder, "messages.yml");

        if (!msgYMLFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        msgYML = YamlConfiguration.loadConfiguration(msgYMLFile);
        msgYML.options().copyDefaults(true);

        // cosmetics.yml
        File file = new File(plugin.getDataFolder(), "cosmetics.yml");

        if (!file.exists()) {
            plugin.saveResource("cosmetics.yml", false);
        }

        cosmeticsYML = YamlConfiguration.loadConfiguration(file);
        cosmeticsYML.options().copyDefaults(true);
    }


    public static void loadConstants() {
        loadConfigYMLs();

        FileConfiguration yaml = plugin.getConfig();

        configYaml = yaml;


        // These support ENV
        thisServer = yaml.getString("this-server.identifier");
        thisSecret = yaml.getString("this-server.secret");

        apiIdentifier = (String) yaml.get("api.identifier");
        apiEndpoint = (String) yaml.get("api.endpoint");
        apiSecret = (String) yaml.get("api.secret");

        // Will update to the environment variable value if present
        thisServer = processIfENV(thisServer);
        thisSecret = processIfENV(thisSecret);
        apiIdentifier = processIfENV(apiIdentifier);
        apiEndpoint = processIfENV(apiEndpoint);
        apiSecret = processIfENV(apiSecret);

        String httpPortString = yaml.getString("http-port", "59152");
        String requestTimeoutMillisString = yaml.getString("api.timeout-millis", "5000");

        httpPortString = processIfENV(httpPortString);
        requestTimeoutMillisString = processIfENV(requestTimeoutMillisString);

        httpPort = Integer.parseInt(httpPortString);
        requestTimeoutMillis = Integer.parseInt(requestTimeoutMillisString);

        for (String s : yaml.getStringList("debugger.enabled-values")) {
            enabledDebugs.add(s.toUpperCase());
        }

        timezone = yaml.getString("timezone");
        partyInvitesExpireAfter = yaml.getInt("parties.invites-expire-after", 60);
        partyMaxSize = Math.max(yaml.getInt("parties.max-players", 9999), 1);
        partyAutoKickAfter = yaml.getInt("parties.autokick-after", 60);
        friendRequestsExpireAfter = yaml.getInt("friends.requests-expire-after", 300);
        dateFormat = yaml.getString("date-format");
        dateShortFormat = yaml.getString("date-short-format");
        timeFormat = yaml.getString("time-format");
        timeShortFormat = yaml.getString("time-short-format");
        permanentString = yaml.getString("punishments.defaults.durations.permanent");
        muteRestrictions = yaml.getStringList("punishments.mute-restrictions");
        ranks = PlayerRank.getRanks();
        playerDefaultListName = yaml.getString("social.player-default-list-name");
        playerDefaultDisplayName = yaml.getString("social.player-default-display-name");
        playerJoinMessage = yaml.getString("social.player-join-message");
        playerLeaveMessage = yaml.getString("social.player-leave-message");

        StringBuilder rankLog = new StringBuilder("Loaded ranks: ");

        for (PlayerRank rank : ranks) {
            rankLog.append(f(rank.getColor())).append(rank.getName()).append(", ");
        }

        Logger.log(rankLog.substring(0, rankLog.length() - 2));

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
        StringBuilder colorLog = new StringBuilder("Loaded color map: ");

        for (Map.Entry<String, String> entry : colorMap.entrySet()) {
            colorLog.append(f(entry.getValue())).append(entry.getKey()).append(", ");
        }

        Logger.log(colorLog.substring(0, colorLog.length() - 2));


        enabledCosmeticTypes = cosmeticsYML.getStringList("enabled-types").stream().map(Cosmetic.CosmeticType::valueOf).collect(Collectors.toList());
        cosmetics = Cosmetic.getCosmetics();
        try {
            ActiveCosmetic.loadActiveCosmetics();
            Logger.log("Loaded cosmetics: " + cosmetics.stream().map(Cosmetic::getId).collect(Collectors.joining(", ")));

        } // Loading so Cosmetics can use this supplementary class.
        catch (InvalidConfigException ex) {
            Logger.logStackTrace(ex);
            Logger.warning("Since an exception was thrown in the ActiveCosmetic loader, some physical cosmetics may not have loaded properly.");
        }

        channels.addAll(CustomChannel.getCustomChannels());
        channels.addAll(CoreChannel.getCoreChannels());
        ChannelRegistry.loadAssociations();

        StringBuilder channelLog = new StringBuilder("&fLoaded chat channels: ");
        for (CoreChannel channel : CoreChannel.getCoreChannels()) {
            channelLog.append(channel.getName()).append("*").append(", ");
        }
        for (CustomChannel channel : CustomChannel.getCustomChannels()) {
            channelLog.append(channel.getName()).append(", ");
        }

        Logger.log(channelLog.substring(0, channelLog.length() - 2));

        chatFilterRules.addAll(ChatFilterRule.getChatFilterRules());
        Logger.log("Loaded chat filter rules: " + chatFilterRules.stream().map(ChatFilterRule::getName).collect(Collectors.joining(", ")));

        shorthandCommands = Shorthand.getShorthandCommands();
        Logger.log("Loaded shorthand commands: " + shorthandCommands.stream().map(Shorthand::getCommandName).collect(Collectors.joining(", ")));
    }

    private static String processIfENV(String s) {
        if (!s.toLowerCase().startsWith("env:")) {
            return s;
        }

        String variableName = s.substring(4);

        String value = System.getenv(variableName);

        if (value == null) {
            throw new RuntimeException(String.format("Could not access environment variable '%s'", variableName));
        }

        return value;
    }
}