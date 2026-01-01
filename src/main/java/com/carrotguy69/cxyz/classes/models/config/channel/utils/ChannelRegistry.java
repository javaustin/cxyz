package com.carrotguy69.cxyz.classes.models.config.channel.utils;

import com.carrotguy69.cxyz.classes.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.classes.models.config.channel.channelTypes.CustomChannel;
import com.carrotguy69.cxyz.classes.models.config.channel.coreChannels.MessageChannel;
import com.carrotguy69.cxyz.classes.models.config.channel.coreChannels.PartyChannel;
import com.carrotguy69.cxyz.classes.models.config.channel.coreChannels.PublicChannel;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

import static com.carrotguy69.cxyz.CXYZ.*;

public class ChannelRegistry {

    public static List<FunctionalChannel> functionalChannels = new ArrayList<>();


    public static void loadAssociations() {
        ConfigurationSection section = configYaml.getConfigurationSection("chat.defaults");

        assert section != null;

        for (String key : section.getKeys(false)) {
            String value = section.getString(key);

            FunctionalChannel fc = new FunctionalChannel(BaseChannel.getChannel(value), ChannelFunction.valueOf(key.toUpperCase()));

            functionalChannels.add(fc);
        }

    }

    public static BaseChannel getChannelByName(String name) {
        BaseChannel baseChannel = null;
        ChannelFunction function = null;
        
        // Get a FunctionalChannel that matches the function, and then return its BaseChannel.
        for (FunctionalChannel channel : functionalChannels) {
            if (channel.getBaseChannel().getName().equalsIgnoreCase(name)) {
                baseChannel = channel.getBaseChannel();
                function = channel.getChannelFunction();
                break;
            }
        }

        if (baseChannel == null) { // No functionality found, use the normal BaseChannel object. (This also means the ChannelFunction will be null)
            BaseChannel customChannel = BaseChannel.getChannel(name);

            return customChannel != null ? new CustomChannel(customChannel) : null;
        }

        switch (function) {
            case PUBLIC:
                return new PublicChannel(baseChannel);

            case PARTY:
                return new PartyChannel(baseChannel);

            case MESSAGE:
                return new MessageChannel(baseChannel);

            default:
                return new CustomChannel(baseChannel);

        }
    }

    public static BaseChannel getChannelByFunction(ChannelFunction function) {
        BaseChannel baseChannel = null;

        // Get a FunctionalChannel that matches the function, and then return its BaseChannel.
        for (FunctionalChannel channel : functionalChannels) {
            if (channel.getChannelFunction().name().equals(function.name())) {
                baseChannel = channel.getBaseChannel();
                break;
            }
        }

        if (baseChannel == null) {
            return null;
        }

        switch (function) {
            case PUBLIC:
                return new PublicChannel(baseChannel);

            case PARTY:
                return new PartyChannel(baseChannel);

            case MESSAGE:
                return new MessageChannel(baseChannel);

            default: // Other channels (punishment, xray, errors, announcements) can be used but don't have custom behavior. So we find their corresponding private/custom channels.
                return new CustomChannel(baseChannel);

        }

    }

    public static String getChannelYMLPath(String channelName) {
        ConfigurationSection section = configYaml.getConfigurationSection("chat");

        assert section != null;

        if (section.contains("core-channels." + channelName)) {
            return "chat.core-channels." + channelName;
        }

        if (section.contains("custom-channels." + channelName)) {
            return "chat.custom-channels." + channelName;
        }

        // No path found
        return null;
    }

    public static void updateChannel(String channelName, BaseChannel newData) {
        // Since we can't modify a channel and expect it to be reflected in the existing lists, we remove it from the list and replace it with our new data.

        for (BaseChannel channel : channels) {
            if (channel.getName().equalsIgnoreCase(channelName)) {
                channels.remove(channel);
                channels.add(newData);
                break;
            }
        }

        for (FunctionalChannel fc : functionalChannels) {
            if (fc.getBaseChannel().getName().equalsIgnoreCase(channelName)) {
                functionalChannels.remove(fc);
                functionalChannels.add(new FunctionalChannel(newData, fc.getChannelFunction()));
                break;
            }
        }
    }

}
