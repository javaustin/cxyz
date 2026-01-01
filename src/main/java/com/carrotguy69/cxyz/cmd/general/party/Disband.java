package com.carrotguy69.cxyz.cmd.general.party;

import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.classes.models.db.Party;
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

import java.util.Map;

public class Disband implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.party.disband";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        Player p = (Player) sender;

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

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
    }
}
