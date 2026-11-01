package com.carrotguy69.cxyz.cmd.party;

import com.carrotguy69.cxyz.cmd.channel.ChannelSet;
import com.carrotguy69.cxyz.models.config.channel.coreChannels.PartyChannel;
import com.carrotguy69.cxyz.models.config.channel.coreChannels.PublicChannel;
import com.carrotguy69.cxyz.models.config.channel.registry.ChannelRegistry;
import com.carrotguy69.cxyz.models.config.channel.registry.ChannelFunction;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Party;
import com.carrotguy69.cxyz.models.config.CommandRestriction;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public class PartyChat implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /party chat <message>
            /party chat hello
        */

        String node = "party.chat";

        if (!sender.hasPermission("cxyz." + node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestriction.handleRestricted(node, sender))
            return true;

        if (!(sender instanceof Player player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        if (args.length == 0) {
            new ChannelSet().onCommand(sender, command, s, String.format("%s", Objects.requireNonNull(ChannelRegistry.getChannelByFunction(ChannelFunction.PARTY)).getName()).split(" "));
            return true;
        }

        chat(player, String.join(" ", args));

        return true;
    }

    public static void chat(Player p, String content) {
        Party party = Party.getPlayerParty(p.getUniqueId());

        NetworkPlayer np = NetworkPlayer.resolvePlayer(p.getUniqueId());

        PartyChannel partyChannel = (PartyChannel) ChannelRegistry.getChannelByFunction(ChannelFunction.PARTY);

        if (partyChannel == null) {
            throw new RuntimeException("Attempted to get the servers party channel but none was found!");
        }

        if (party == null) {
            if (np.getChatChannel() != null && Objects.equals(np.getChatChannel().getName(), partyChannel.getName())) {
                PublicChannel publicChannel = (PublicChannel) ChannelRegistry.getChannelByFunction(ChannelFunction.PUBLIC);

                if (publicChannel == null) {
                    throw new RuntimeException("Attempted to get the servers public channel but none was found!");
                }

                np.setChatChannel(publicChannel);
                np.sync();

                MessageUtils.sendParsedMessage(p, MessageKey.PARTY_ERROR_CHANNEL_CHANGED, MapFormatters.playerFormatter(np));
                return;
            }

            MessageUtils.sendParsedMessage(p, MessageKey.PARTY_ERROR_NOT_IN_PARTY, Map.of());
            return;
        }

        if (!np.isMuted())
            party.chat(np, content);
    }

}
