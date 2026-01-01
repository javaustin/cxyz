package com.carrotguy69.cxyz.cmd.general.party;

import com.carrotguy69.cxyz.classes.models.config.channel.coreChannels.PartyChannel;
import com.carrotguy69.cxyz.classes.models.config.channel.coreChannels.PublicChannel;
import com.carrotguy69.cxyz.classes.models.config.channel.utils.ChannelRegistry;
import com.carrotguy69.cxyz.classes.models.config.channel.utils.ChannelFunction;
import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.classes.models.db.Party;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.template.MapFormatters;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public class Chat implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.party.chat";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        Player player = ((Player) sender);

        // throw CommandExceptions in async blocks only. Use regular messages outside of it.
        if (args.length == 0) {
            MessageUtils.sendParsedMessage(player, MessageKey.MISSING_CONTENT, Map.of());
            return true;
        }

        chat(player, String.join(" ", args));

        return true;
    }

    public static void chat(Player p, String content) {
        Party party = Party.getPlayerParty(p.getUniqueId());

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        PartyChannel partyChannel = (PartyChannel) ChannelRegistry.getChannelByFunction(ChannelFunction.PARTY);

        if (partyChannel == null) {
            throw new RuntimeException("partyChannel is null once again");
        }

        if (party == null) {
            if (np.getChatChannel() != null && Objects.equals(np.getChatChannel().getName(), partyChannel.getName())) {
                PublicChannel publicChannel = (PublicChannel) ChannelRegistry.getChannelByFunction(ChannelFunction.PUBLIC);

                if (publicChannel == null) {
                    throw new RuntimeException("publicChannel is null once again");
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
