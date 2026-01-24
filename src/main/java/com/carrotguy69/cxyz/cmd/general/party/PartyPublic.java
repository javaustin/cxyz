package com.carrotguy69.cxyz.cmd.general.party;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Party;
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
import java.util.Objects;

public class PartyPublic implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /party private
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.party.public";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        Player p  = (Player) sender;

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        setPrivate(np);

        return true;
    }

    public void setPrivate(NetworkPlayer np) {
        Party party = Party.getPlayerParty(np.getUUID());

        if (party == null) {
            MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.PARTY_ERROR_NOT_IN_PARTY, Map.of());
            return;
        }

        if (!Objects.equals(party.getOwnerUUID(), np.getUUID())) {
            MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.PARTY_ERROR_LEADER_ONLY, MapFormatters.partyFormatter(party));
            return;
        }

        if (party.isPublic()) {
            MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.PARTY_ERROR_ALREADY_PUBLIC, MapFormatters.partyFormatter(party));
            return;
        }


        party.setPublic(true);

        MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.PARTY_SET_PUBLIC_TRUE, MapFormatters.partyFormatter(party));

        party.sync();
    }
}