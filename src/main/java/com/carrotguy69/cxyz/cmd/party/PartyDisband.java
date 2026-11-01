package com.carrotguy69.cxyz.cmd.party;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Party;
import com.carrotguy69.cxyz.models.config.CommandRestriction;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.parties;

public class PartyDisband implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        /*
        SYNTAX:
            /party disband
        */

        String node = "party.disband";

        if (!sender.hasPermission("cxyz." + node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestriction.handleRestricted(node, sender))
            return true;

        if (!(sender instanceof Player p)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        NetworkPlayer np = NetworkPlayer.resolvePlayer(p.getUniqueId());

        disband(np);

        return true;
    }

    private static void disband(NetworkPlayer np) {
        Player p = np.getPlayer();

        if (!np.isInParty()) {
            MessageUtils.sendParsedMessage(p, MessageKey.PARTY_ERROR_NOT_IN_PARTY, Map.of());
            return;
        }

        Party party = Party.getPlayerParty(p.getUniqueId());

        if (party == null)
            throw new NullPointerException("Party was supposed to be non-null but became null somehow!");

        Map<String, Object> commonMap = MapFormatters.partyFormatter(party);

        if (!Party.isPartyOwner(p.getUniqueId())) {
            MessageUtils.sendParsedMessage(p, MessageKey.PARTY_ERROR_LEADER_ONLY, commonMap);
            return;
        }

        party.announce(MessageGrabber.grab(MessageKey.PARTY_DISBAND), Map.of("prefix", commonMap));

        party.delete(); // Handles sending updated data to API, and updates map.
        parties.remove(party.getOwnerUUID(), party);
    }
}
