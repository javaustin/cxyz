package com.carrotguy69.cxyz.cmd.general.channel;

import com.carrotguy69.cxyz.classes.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.template.MapFormatters;
import com.carrotguy69.cxyz.other.messages.MessageGrabber;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class UnignoreChannel implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        String node = "cxyz.general.channel.unignore";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        Player p = (Player) sender;


        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        List<String> allowedChannels = new ArrayList<>(BaseChannel.getChannelNames(true));

        Map<String, Object> commonMap = new HashMap<>();

        if (args.length == 0) { // List the muted channels
            String format = MessageGrabber.grab(MessageKey.CHAT_CHANNEL_LIST_CHANNEL_FORMAT);
            String delimiter = MessageGrabber.grab(MessageKey.CHAT_CHANNEL_LIST_CHANNEL_SEPARATOR);

            List<String> formattedChannels = new ArrayList<>();

            for (int i = 0; i < np.getMutedChannels().size(); i++) { // Create a string using the rank format template. We will replace the
                String channelName = np.getMutedChannels().get(i);

                String channelString = format;

                channelString = channelString.replace("{channel-name}", "{channel-name-" + i + "}");
                channelString = channelString.replace("{channel-prefix}", "{channel-prefix-" + i + "}");

                formattedChannels.add(channelString);


                BaseChannel bc = BaseChannel.getChannel(channelName);
                if (bc == null) { // Not all channelName's will be PrivateChannel. Because of PUBLIC, PARTY, and MESSAGE chats
                    commonMap.put("channel-name-" + i, channelName.toLowerCase());
                    commonMap.put("channel-prefix-" + i, channelName.toLowerCase());
                }
                else {
                    commonMap.put("channel-name-" + i, bc.getName());
                    commonMap.put("channel-prefix-" + i, bc.getPrefix() != null && !bc.getPrefix().isBlank() ? bc.getPrefix() : bc.getName());
                }
            }

            String channelListString = String.join(delimiter, formattedChannels); // This returns the interactive formatting of the joined rank list.

            String unparsed = MessageGrabber.grab(MessageKey.CHAT_CHANNEL_LIST_IGNORED);

            unparsed = unparsed.replace("{ignored-channels}", !np.getMutedChannels().isEmpty() ? channelListString : "None");

            MessageUtils.sendParsedMessage(sender, unparsed, commonMap);

            return true;
        }

        else {
            BaseChannel baseChannel = BaseChannel.getChannel(args[0]);


            if (!allowedChannels.contains(args[0].toLowerCase()) || baseChannel == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_CHANNEL, Map.of("input", args[0]));
                return true;
            }

            commonMap = MapFormatters.channelFormatter(baseChannel);

            if (!np.isMutingChannel(baseChannel)) {
                MessageUtils.sendParsedMessage(sender, MessageKey.CHAT_CHANNEL_NOT_IGNORED, commonMap);
                return true;
            }

//            if (!baseChannel.isIgnorable()) {
//                MessageUtils.sendParsedMessage(sender, MessageKey.CHAT_CHANNEL_NOT_IGNORABLE, commonMap);
//                return true;
//            }


            np.unmuteChannel(args[0].toLowerCase());
            np.sync();

            MessageUtils.sendParsedMessage(sender, MessageKey.CHAT_CHANNEL_UNIGNORED, commonMap);
        }


        return true;
    }
}
