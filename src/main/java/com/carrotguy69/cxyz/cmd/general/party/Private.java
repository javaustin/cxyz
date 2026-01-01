package com.carrotguy69.cxyz.cmd.general.party;

import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.classes.models.db.Party;
import com.carrotguy69.cxyz.classes.models.db.PartyInvite;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.template.MapFormatters;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.carrotguy69.cxyz.CXYZ.partyInvites;

public class Private implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.party.private";

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

    public void setPrivate(NetworkPlayer inviter) {
        Party party = Party.getPlayerParty(inviter.getUUID());

        if (party == null) {
            MessageUtils.sendParsedMessage(inviter.getPlayer(), MessageKey.PARTY_ERROR_NOT_IN_PARTY, Map.of());
            return;
        }

        if (!Objects.equals(party.getOwnerUUID(), inviter.getUUID())) {
            MessageUtils.sendParsedMessage(inviter.getPlayer(), MessageKey.PARTY_ERROR_LEADER_ONLY, MapFormatters.partyFormatter(party));
            return;
        }

        party.setPublic(false);

        MessageUtils.sendParsedMessage(inviter.getPlayer(), MessageKey.PARTY_SET_PUBLIC_FALSE, MapFormatters.partyFormatter(party));

        party.sync();

        // Remove all existing invites.


        Collection<PartyInvite> invites = partyInvites.get(inviter.getUUID());
        List<PartyInvite> toDelete = new ArrayList<>();

        for (PartyInvite invite : invites) {
            if (invite != null && Objects.equals(invite.getInviterUUID(), inviter.getUUID())) {
                toDelete.add(invite); // Collect invites for deletion
            }
        }

        for (PartyInvite markedInvite : toDelete)
            markedInvite.delete(); // Send a request to the API to delete this invite.

        partyInvites.removeAll(inviter.getUUID()); // Remove all invites from the inviter when we are done sending requests.




    }
}
