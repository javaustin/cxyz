package com.carrotguy69.cxyz.other;

import com.carrotguy69.cxyz.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.utils.ObjectUtils;
import com.carrotguy69.cxyz.other.webhook.DiscordWebhook;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Logger {
    public enum DefaultChannelType { // default types
        PUNISHMENT,
        XRAY,
        ERROR,
        PUBLIC,
        PARTY,
        MESSAGE
    }

    private static final Path latestLog = Paths.get("logs/latest.log");

    private static void toLatestLog(String text) {

        Bukkit.getScheduler().runTask(plugin, () -> {
            try (BufferedWriter writer = Files.newBufferedWriter(latestLog, StandardOpenOption.APPEND)) {
                writer.write("[" + java.time.LocalDateTime.now() + "] [Server thread/INFO]: " + text);
                writer.newLine();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void logStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));

        plugin.getLogger().severe(ex.getMessage());
        String join = String.join("\n", sw.toString().split("\n")); // Stack-trace text

        plugin.getLogger().info(join);
        toLatestLog(join);
    }

    public static void log(String content) {
        info(content);
    }

    private static void internalLog(DefaultChannelType type, String content) {

        String channelName = configYaml.getString("chat.defaults." + type.name().toLowerCase()); // Get the channel in config.yml that is set to record this LogEntryType

        BaseChannel channel;

        if (channelName == null || channelName.isEmpty()) {
            return;
        }
        else {
            channel = BaseChannel.getChannel(channelName);
        }

        if (channel == null) { // The config provided a non-existent channel
            Logger.warning(String.format("Attempted to log a %s, but there was no channel defined for it in config.yml (chat.defaults.)! Ignoring...", type.name()));
            return;
        }

        Map<String, Object> commonMap = MapFormatters.channelFormatter(channel);
        commonMap.put("content", content);
        commonMap.put("message", content);

        channel.sendChannelMessage("{message}", commonMap);

        if (channel.isConsoleEnabled()) {
            MessageUtils.sendParsedMessage(Bukkit.getConsoleSender(), "{channel-prefix}{message}", commonMap);
        }


        String url = channel.getWebhookURL();

        if (url != null && !url.isEmpty()) {
            new DiscordWebhook().setURL(url).setContent(
                    ObjectUtils.formatPlaceHolders(content, Map.of("prefix", channel.getPrefix()))
            ).send();
        }
    }

    public static void punishment(String content) {
        Logger.internalLog(DefaultChannelType.PUNISHMENT, content);
    }

    public static void xray(String content) {
        Logger.internalLog(DefaultChannelType.XRAY, content);
    }

    public static void error(String content) {
        Logger.internalLog(DefaultChannelType.ERROR, content);
    }

    public static void debugMessage(String content) {
        if (!ObjectUtils.containsIgnoreCase(enabledDebugs, "message_parser")) {
            return;
        }

        Logger.info("[DEBUG] " + content);
    }

    public static void debugFailedRequest(String content) {
        if (!ObjectUtils.containsIgnoreCase(enabledDebugs, "failed_requests")) {
            return;
        }

        Logger.warning("[DEBUG] " + content);
    }

    public static void debugAllRequest(String content) {
        if (!ObjectUtils.containsIgnoreCase(enabledDebugs, "all_requests")) {
            return;
        }

        Logger.info("[DEBUG] " + content);
    }

    public static void debugShorthand(String content) {
        if (!ObjectUtils.containsIgnoreCase(enabledDebugs, "shorthand_commands")) {
            return;
        }

        Logger.info("[DEBUG] " + content);
    }

    public static void debugPunishment(String content) {
        if (!ObjectUtils.containsIgnoreCase(enabledDebugs, "punishment")) {
            return;
        }

        Logger.info("[DEBUG] " + content);
    }



    public static void info(String content) {
        Bukkit.getScheduler().runTask(plugin, () -> plugin.getLogger().info(content));
    }

    public static void warning(String content) {
        Bukkit.getScheduler().runTask(plugin, () -> plugin.getLogger().warning(content));
    }

    public static void severe(String content) {
        Bukkit.getScheduler().runTask(plugin, () -> plugin.getLogger().severe(content));
    }

}
