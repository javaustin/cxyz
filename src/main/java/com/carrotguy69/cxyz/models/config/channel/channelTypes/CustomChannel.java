package com.carrotguy69.cxyz.models.config.channel.channelTypes;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Punishment;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;

import net.md_5.bungee.api.ChatColor;

import com.carrotguy69.cxyz.exceptions.InvalidConfigException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;

import static com.carrotguy69.cxyz.CXYZ.*;

public class CustomChannel extends BaseChannel {

    public CustomChannel(BaseChannel channel) {
        this.channelName = channel.getName();
        this.channelPrefix = channel.getPrefix();
        this.chatFormat = channel.getChatFormat();
        this.webhookURL = channel.getWebhookURL();
        this.triggerPrefix = channel.getTriggerPrefix();
        this.console = channel.isConsoleEnabled();
        this.ignorable = channel.isIgnorable();
        this.lockable = channel.isLockable();
        this.locked = channel.isLocked();
        this.readOnly = channel.isReadOnly();
        this.aliases = channel.getAliases();
    }

    public CustomChannel(String name, String prefix, String chatFormat, String webhookURL, String triggerPrefix, boolean console, boolean ignorable, boolean lockable, boolean locked, boolean readOnly, List<String> aliases) {
        super(name, prefix, chatFormat, webhookURL, triggerPrefix, console, ignorable, lockable, locked, readOnly, aliases);
    }

    public static List<CustomChannel> getCustomChannels() {
        try {
            List<CustomChannel> customChannels = new ArrayList<>();

            ConfigurationSection section = configYaml.getConfigurationSection("chat.custom-channels");

            if (section == null) {
                throw new InvalidConfigException("config.yml", "chat.custom-channels", "Failed to load custom channels because there are no channels defined in config.yml.");
            }

            Set<String> channelNames = section.getKeys(false);

            if (channelNames.isEmpty()) {
                throw new InvalidConfigException("config.yml", "chat.custom-channels", "No channels defined under chat.custom-channels in config.yml.");
            }

            for (String name : channelNames) {
                String channelNode = "chat.custom-channels." + name;

                String prefix = configYaml.getString(channelNode + ".prefix");
                String triggerPrefix = configYaml.getString(channelNode + ".trigger-prefix");
                String webhookURL = configYaml.getString(channelNode + ".webhook-url");
                String chatFormat = configYaml.getString(channelNode + ".chat-format");
                boolean readOnly = configYaml.getBoolean(channelNode + ".read-only");
                boolean console = configYaml.getBoolean(channelNode + ".console");
                boolean ignorable = configYaml.getBoolean(channelNode + ".ignorable");
                boolean lockable = configYaml.getBoolean(channelNode + ".lockable");
                boolean locked = configYaml.getBoolean(channelNode + ".locked");
                List<String> aliases = configYaml.getStringList(channelNode + ".aliases");

                CustomChannel ch = new CustomChannel(name, prefix, chatFormat, webhookURL, triggerPrefix, console, ignorable, lockable, locked, readOnly, aliases);

                customChannels.add(ch);
            }

            return customChannels;
        }
        catch (InvalidConfigException ex) {
            Logger.warning("An InvalidConfigException was thrown in startup. Private channels will not load.");
        }

        return List.of();
    }

    @Override
    public void onChat(AsyncPlayerChatEvent e) {
        Logger.debugMessage("called CustomChannel.onChat(...)");

        Player p = e.getPlayer();

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        Map<String, Object> commonMap = MapFormatters.channelFormatter(this);
        commonMap.putAll(MapFormatters.playerFormatter(np));

        String content = ChatColor.stripColor(f(e.getMessage()));

        commonMap.put("content", content);
        commonMap.put("message", content);

        boolean blocked = this.evaluateContent(p, content, commonMap);

        if (blocked)
            return;


        if (!np.canAccessChannel(this)) {
            MessageUtils.sendParsedMessage(p, MessageKey.CHAT_CHANNEL_NO_ACCESS, commonMap);
            return;
        }

        if (this.isLockable() && this.isLocked()) {
            MessageUtils.sendParsedMessage(p, MessageKey.CHAT_CHANNEL_IS_LOCKED, commonMap);
            return;
        }

        if (this.isReadOnly()) {
            MessageUtils.sendParsedMessage(p, MessageKey.CHAT_CHANNEL_READ_ONLY, commonMap);
            return;
        }

        if (np.isMutingChannel(this) && this.isIgnorable()) {
            MessageUtils.sendParsedMessage(p, MessageKey.CHAT_CHANNEL_IS_MUTED, commonMap);
            return;
        }

        if (content.isBlank()) {
            MessageUtils.sendParsedMessage(p, MessageKey.MISSING_CONTENT, commonMap);
            return;
        }

        if (np.isMuted()) {
            Punishment punishment = Punishment.getActivePunishment(np, Punishment.PunishmentType.MUTE);

            if (punishment != null) {
                MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.PUNISHMENT_MUTE_PLAYER_MESSAGE, MapFormatters.punishmentFormatter(np.getPlayer(), punishment));
                return;
            }
        }


        this.sendChannelMessage(this.getChatFormat(), commonMap);

    }

    @Override
    public void onSelect(NetworkPlayer np) {
        Logger.debugMessage("called CustomChannel.onSelect(...)");


        if (!np.canAccessChannel(this)) {
            MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.CHAT_CHANNEL_NO_ACCESS, MapFormatters.channelFormatter(this));
            return;
        }

        np.setChatChannel(this);
        np.sync();

        MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.CHAT_CHANNEL_SET, MapFormatters.channelFormatter(this));

    }


}
