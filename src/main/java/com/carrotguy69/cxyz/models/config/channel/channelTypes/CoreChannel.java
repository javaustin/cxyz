package com.carrotguy69.cxyz.models.config.channel.channelTypes;

import com.carrotguy69.cxyz.exceptions.InvalidConfigException;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;

import static com.carrotguy69.cxyz.CXYZ.*;

public class CoreChannel extends BaseChannel {

    public CoreChannel(String name, String prefix, String chatFormat, String webhookURL, String triggerPrefix, boolean console, boolean ignorable, boolean lockable, boolean locked, boolean readOnly, List<String> aliases) {
        super(name, prefix, chatFormat, webhookURL, triggerPrefix, console, ignorable, lockable, locked, readOnly, aliases);
    }

    public CoreChannel() {} // ok keep this

    @Override
    public void onChat(AsyncPlayerChatEvent e) {
        throw new UnsupportedOperationException("Method 'onChat' must be overridden by an existing CoreChannel. It should not be called from an instance of this base class.");
    }

    @Override
    public void onSelect(NetworkPlayer np) {
        throw new UnsupportedOperationException("Method 'onSelect' must be overridden by an existing CoreChannel. It should not be called from an instance of this base class.");
    }


    public static List<CoreChannel> getCoreChannels() {
        try {
            List<CoreChannel> coreChannels = new ArrayList<>();

            ConfigurationSection section = configYaml.getConfigurationSection("chat.core-channels");

            if (section == null) {
                throw new InvalidConfigException("config.yml", "chat.core-channels", "Failed to load core channels because there are no channels defined in config.yml.");
            }

            Set<String> channelNames = section.getKeys(false);


            for (String name : channelNames) {
                String channelNode = "chat.core-channels." + name;

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

                CoreChannel ch = new CoreChannel(name, prefix, chatFormat, webhookURL, triggerPrefix, console, ignorable, lockable, locked, readOnly, aliases);

                coreChannels.add(ch);
            }

            return coreChannels;
        }
        catch (InvalidConfigException ex) {
            Logger.warning("An InvalidConfigException was thrown in startup. Core channels (public, party, messages) will not load.");
        }

        return List.of();
    }

}
