package com.carrotguy69.cxyz.cmd.admin.channel;

import com.carrotguy69.cxyz.classes.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.classes.models.config.channel.utils.ChannelFunction;
import com.carrotguy69.cxyz.classes.models.config.channel.utils.ChannelRegistry;
import com.carrotguy69.cxyz.other.messages.MessageGrabber;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.template.MapFormatters;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Unlock implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.admin.unlock";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        // The default channel admins will want to lock most often is the public channel.
        BaseChannel channel = ChannelRegistry.getChannelByFunction(ChannelFunction.PUBLIC);

        if (args.length > 0) {
            // If args are provided then we will try to get the specified channel.

            channel = BaseChannel.getChannel(args[0]);
        }

        if (channel == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_CHANNEL, Map.of("input", args[0]));
            return true;
        }

        Map<String, Object> commonMap = MapFormatters.channelFormatter(channel);

        if (!channel.isLocked()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.CHAT_CHANNEL_ALREADY_UNLOCKED, commonMap);
            return true;
        }

        channel.setLocked(false);
        ChannelRegistry.updateChannel(channel.getName(), channel);


        String path = ChannelRegistry.getChannelYMLPath(channel.getName());

        configYaml.set(path + ".locked", false);
        instance.saveConfig();

        commonMap = MapFormatters.channelFormatter(channel); // Because the object was updated, we use a new MapFormatter.
        commonMap.putAll(MapFormatters.senderFormatter(sender));

        channel.sendChannelMessage(MessageGrabber.grab(MessageKey.CHAT_CHANNEL_UNLOCKED), commonMap);

        return true;
    }
}
