package com.carrotguy69.cxyz.models.config.channel.channelTypes;

import com.carrotguy69.cxyz.http.Request;
import com.carrotguy69.cxyz.models.config.ChatFilterRule;
import com.carrotguy69.cxyz.models.config.GameServer;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.other.webhook.DiscordWebhook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.carrotguy69.cxyz.CXYZ.*;
import static com.carrotguy69.cxyz.CXYZ.gson;
import static com.carrotguy69.cxyz.CXYZ.this_server;
import static com.carrotguy69.cxyz.messages.MessageUtils.formatPlaceholders;

public abstract class BaseChannel {
    public String channelName;
    public String channelPrefix;
    public String chatFormat;
    public String webhookURL;
    public String triggerPrefix;

    public boolean console;
    public boolean ignorable;
    public boolean lockable;
    public boolean locked;
    public boolean readOnly;

    public List<String> aliases;

    public BaseChannel(String name, String prefix, String chatFormat, String webhookURL, String triggerPrefix, boolean console, boolean ignorable, boolean lockable, boolean locked, boolean readOnly, List<String> aliases) {
        this.channelName = name;
        this.channelPrefix = prefix;
        this.chatFormat = chatFormat;
        this.webhookURL = webhookURL;
        this.triggerPrefix = triggerPrefix;
        this.console = console;
        this.ignorable = ignorable;
        this.lockable = lockable;
        this.locked = locked;
        this.readOnly = readOnly;
        this.aliases = aliases;
    }

    protected BaseChannel() {
    }

    public String getName() {
        return channelName;
    }

    public String getPrefix() {
        return channelPrefix;
    }

    public String getChatFormat() {
        return chatFormat;
    }

    public String getWebhookURL() {
        return webhookURL;
    }

    public String getTriggerPrefix() {
        return triggerPrefix;
    }

    public boolean isConsoleEnabled() {
        return console;
    }

    public boolean isIgnorable() {
        return ignorable;
    }

    public boolean isLockable() {
        return lockable;
    }

    public boolean isLocked(){
        return locked;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public abstract void onChat(AsyncPlayerChatEvent e);

    public abstract void onSelect(NetworkPlayer np);

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setChannelPrefix(String channelPrefix) {
        this.channelPrefix = channelPrefix;
    }

    public void setChatFormat(String chatFormat) {
        this.chatFormat = chatFormat;
    }

    public void setTriggerPrefix(String triggerPrefix) {
        this.triggerPrefix = triggerPrefix;
    }

    public void setWebhookURL(String webhookURL) {
        this.webhookURL = webhookURL;
    }

    public void setConsole(boolean console) {
        this.console = console;
    }

    public void setIgnorable(boolean ignorable) {
        this.ignorable = ignorable;
    }

    public void setLockable(boolean lockable) {
        this.lockable = lockable;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public boolean evaluateContent(Player p, String content, Map<String, Object> commonMap) {
        // Evaluates the message using the ChatFilterRules provided for the channel. Returns true if a rule was broken.
        List<ChatFilterRule> rules = ChatFilterRule.getRulesForChannel(this);

        for (ChatFilterRule rule : rules) {
            for (String word : rule.getBlacklistedWords()) {
                if (!content.toLowerCase().contains(word.toLowerCase()))
                    continue;

                if (p.hasPermission("cxyz.chat-filter." + rule.getName() + ".bypass"))
                    continue;

                Logger.log(String.format("Blocked word: %s. Dispatching actions...", word));


                // was getting async errors so i wrapped this in a task
                Bukkit.getScheduler().runTask(plugin, () -> {
                    for (String action : rule.getActions()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), MessageUtils.formatPlaceholders(action, commonMap));
                    }
                });


                return true;
            }
        }

        return false;
    }

    public String toString() {

        return "BaseChannel{" +
                "channelName=\"" + channelName + "\", " +
                "channelPrefix=\"" + channelPrefix + "\", " +
                "chatFormat=\"" + chatFormat + "\", " +
                "triggerPrefix=\"" + triggerPrefix + "\", " +
                "console=\"" + console + "\", " +
                "ignorable=" + ignorable + ", " +
                "lockable=" + lockable + ", " +
                "locked=" + locked + ", " +
                "readOnly=" + readOnly + ", " +
                "aliases=" + aliases +
                "}";

    }

    public static List<BaseChannel> getAllChannels() {
        return channels;
    }

    public static List<String> getChannelNames(boolean includeAliases) {
        List<String> results = new ArrayList<>();

        for (BaseChannel channel : getAllChannels()) {
            results.add(channel.getName());
            if (includeAliases)
                results.addAll(channel.getAliases());
        }

        return results;
    }

    public static BaseChannel getChannel(String name) {
        for (BaseChannel channel : getAllChannels()) {
            if (channel.getName().equalsIgnoreCase(name)) {

                return channel;
            }

            for (String alias : channel.getAliases()) {
                if (alias.equalsIgnoreCase(name)) {
                    return channel;
                }
            }
        }

        return null;
    }


    public void sendChannelMessage(String chatFormat, Map<String, Object> formatMap) {
        // This function is not to be called by the HTTP listener, because it will send out requests to other servers.

        for (GameServer server : servers) {
            if (Objects.equals(server.getName(), this_server.getName())) {
                sendChannelMessage(this, chatFormat, formatMap);
                continue;
            }

            Request.postRequest(server.getIP() + "/sendChannelMessage", gson.toJson(
                    Map.of(
                            "channel", this.getName(),
                            "chatFormat", chatFormat,
                            "formatMap", formatMap
                    )
            ));

        }

        if (!this.getWebhookURL().isBlank()) {
            String url = this.getWebhookURL();

            DiscordWebhook webhook = new DiscordWebhook()
                    .setURL(url)
                    .setContent(
                            ChatColor.stripColor(formatPlaceholders(chatFormat, formatMap))
                    );

            webhook.send();
        }
    }


    // Static functions

    public static void sendChannelMessage(BaseChannel channel, String chatFormat, Map<String, Object> formatMap) {
        // This is the function that can be called by the HTTP listener, because it won't make any other requests.

        for (Player p : Bukkit.getOnlinePlayers()) {
            NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

            if (np.canAccessChannel(channel) && !np.isMutingChannel(channel)) {
                MessageUtils.sendParsedMessage(p, chatFormat, formatMap);
            }

            if (channel.isConsoleEnabled()) {
                MessageUtils.sendParsedMessage(Bukkit.getConsoleSender(), chatFormat, formatMap);
            }
        }

    }
}
