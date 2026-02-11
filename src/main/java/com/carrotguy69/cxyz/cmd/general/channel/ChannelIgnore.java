package com.carrotguy69.cxyz.cmd.general.channel;

import com.carrotguy69.cxyz.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ChannelIgnore implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /channel ignore <channel>
            /channel ignore public
        */

        String node = "cxyz.general.channel.ignore";
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

        if (args.length == 0) { // List the muted channels
            ChannelIgnoreList.channelIgnoreList(sender, np, 1);
            return true;
        }

        else {
            BaseChannel baseChannel = BaseChannel.getChannel(args[0]);


            if (!allowedChannels.contains(args[0].toLowerCase()) || baseChannel == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_CHANNEL, Map.of("input", args[0]));
                return true;
            }

            Map<String, Object> commonMap = MapFormatters.channelFormatter(baseChannel);

            if (np.isMutingChannel(baseChannel)) {
                MessageUtils.sendParsedMessage(sender, MessageKey.CHAT_CHANNEL_ALREADY_IGNORED, commonMap);
                return true;
            }

            if (!baseChannel.isIgnorable()) {
                MessageUtils.sendParsedMessage(sender, MessageKey.CHAT_CHANNEL_NOT_IGNORABLE, commonMap);
                return true;
            }


            np.muteChannel(args[0].toLowerCase());
            np.sync();

            MessageUtils.sendParsedMessage(sender, MessageKey.CHAT_CHANNEL_IGNORED, commonMap);
        }


        return true;
    }
}
