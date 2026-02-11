package com.carrotguy69.cxyz.cmd.general.channel;

import com.carrotguy69.cxyz.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.models.config.channel.utils.ChannelRegistry;
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

import java.util.Map;

public class ChannelSet implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /channel set <channel>
            /channel <channel>
            /channel set public
            /channel public
        */

        String node = "cxyz.general.channel.set";

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId());


        Map<String, Object> commonMap = MapFormatters.channelFormatter(np.getChatChannel());


        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.CHAT_CHANNEL_VIEW, commonMap);

            return true;
        }

        BaseChannel channel = BaseChannel.getChannel(args[0]);

        if (channel == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_CHANNEL, Map.of("input", args[0]));
            return true;
        }

        commonMap = MapFormatters.channelFormatter(channel);

        if (!np.canAccessChannel(channel)) {
            // We should also restrict what chat channel a user can set for themselves in set: /channel <channel>. (Can be easily done through simple permission checks.)
            // However, we will keep this as a fallback for edge cases (i.e., A player set their chat channel to a private one but now no longer has access)

            MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.CHAT_CHANNEL_NO_ACCESS, commonMap);
            return true;
        }

        if (channel.isReadOnly()) {
            MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.CHAT_CHANNEL_READ_ONLY, commonMap);
            return true;
        }

        BaseChannel ch = ChannelRegistry.getChannelByName(channel.getName());

        if (ch == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_CHANNEL, Map.of("input", args[0]));
            return true;
        }


        ch.onSelect(np);

        return true;
    }

}
