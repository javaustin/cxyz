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

import static com.carrotguy69.cxyz.CXYZ.parties;
import static com.carrotguy69.cxyz.CXYZ.partiesEnabled;

public class PartyCreate implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /party create
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.party.create";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (!partiesEnabled) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PARTY_DISABLED, Map.of());
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        Player player = ((Player) sender);

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(player.getUniqueId());

        createParty(np);

        return true;
    }

    private static void createParty(NetworkPlayer np) {
        Player p = np.getPlayer();

        if (np.isInParty()) {
            Party party = Party.getPlayerParty(np.getUUID());

            assert party != null; // because np.isInParty() checks for this.

            MessageUtils.sendParsedMessage(p, MessageKey.PARTY_ERROR_IN_PARTY_CREATE, MapFormatters.partyFormatter(party));
            return;
        }

        Party party = new Party(np.getUUID().toString(), "[]", 0);
        // Using any form of Party.sync(...) and then immediately trying to add a player to it will create a race condition.
        // Wait until party data is finalized, then sync.

        parties.put(np.getUUID(), party);
        party.create();


        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);

        MessageUtils.sendParsedMessage(p, MessageKey.PARTY_CREATED, commonMap);
    }

}
