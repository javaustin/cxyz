package com.carrotguy69.cxyz.cmd.general.party;


import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Party;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.utils.TimeUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static com.carrotguy69.cxyz.CXYZ.*;


public class PartyInvite implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        /*
        SYNTAX:
            /party invite <player>
            /party invite Notch
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.party.invite";

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

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
            return true;
        }

        Player p = (Player) sender;

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        invite(np, args[0]);

        return true;
    }

    public void invite(NetworkPlayer inviter, String recipientName) {
        Party party = Party.getPlayerParty(inviter.getUUID());

        if (party == null) { // We seamlessly auto-create the party
            party = new Party(inviter.getUUID().toString(), "[]", 0);
            party.create(); // Since inviting a player won't add them to the party (without them accepting it), we post the new empty party to the API.
        }

        Map<String, Object> commonMap = MapFormatters.partyFormatter(party);

        if (!Objects.equals(party.getOwnerUUID(), inviter.getUUID())) { // Player is already in a party, but not the leader
            MessageUtils.sendParsedMessage(inviter.getPlayer(), MessageKey.PARTY_ERROR_LEADER_ONLY, commonMap);
            return;
        }

        NetworkPlayer recipient = NetworkPlayer.getPlayerByUsername(recipientName);

        if (recipient == null) {
            MessageUtils.sendParsedMessage(inviter.getPlayer(), MessageKey.PLAYER_NOT_FOUND, Map.of("username", recipientName));
            return;
        }

        if (Objects.equals(inviter.getUUID(), recipient.getUUID())) {
            MessageUtils.sendParsedMessage(inviter.getPlayer(), MessageKey.PLAYER_IS_SELF, Map.of());
            return;
        }

        if (!recipient.isOnline() || (recipient.isOnline() && !recipient.isVisibleTo(inviter))) {
            MessageUtils.sendParsedMessage(inviter.getPlayer(), MessageKey.PLAYER_IS_OFFLINE, MapFormatters.playerFormatter(recipient));
            return;
        }

        if (recipient.getPartyPrivacy().equals(NetworkPlayer.PartyInvitePrivacy.DISALLOWED)
             || (recipient.getPartyPrivacy().equals(NetworkPlayer.PartyInvitePrivacy.FRIENDS_ONLY)
             && !recipient.isFriendsWith(inviter))
                                            ) {
            MessageUtils.sendParsedMessage(inviter.getPlayer(), MessageKey.PARTY_INVITE_FAIL, MapFormatters.playerFormatter(recipient));
            return;
        }

        if (com.carrotguy69.cxyz.models.db.PartyInvite.getLastInvite(inviter, recipient) != null || Objects.equals(Party.getPlayerParty(recipient.getUUID()), party)) {
            commonMap.putAll(MapFormatters.playerFormatter(recipient));
            MessageUtils.sendParsedMessage(inviter.getPlayer(), MessageKey.PARTY_ERROR_DUPLICATE_INVITE, commonMap);
            return;
        }


        com.carrotguy69.cxyz.models.db.PartyInvite partyInvite = new com.carrotguy69.cxyz.models.db.PartyInvite(
                    inviter.getUUID().toString(),
                    recipient.getUUID().toString(),
                    TimeUtils.unixTimeNow() + partyInvitesExpireAfter
        );

        if (partyInvites.containsKey(inviter.getUUID())) {
            // Do not allow duplicate invites
            Collection<com.carrotguy69.cxyz.models.db.PartyInvite> invites = partyInvites.get(inviter.getUUID());

            for (com.carrotguy69.cxyz.models.db.PartyInvite invite : invites) {
                if (Objects.equals(invite.getRecipientUUID(), recipient.getUUID()) && invite.getExpireTimestamp() > TimeUtils.unixTimeNow()) {
                    commonMap.putAll(MapFormatters.playerFormatter(recipient));
                    MessageUtils.sendParsedMessage(inviter.getPlayer(), MessageKey.PARTY_ERROR_DUPLICATE_INVITE, commonMap);
                    return;
                }
            }
        }

        if (party.size() + 1 >= partyMaxSize) {
            MessageUtils.sendParsedMessage(inviter.getPlayer(), MessageKey.PARTY_FULL, commonMap);
            return;
        }

        partyInvite.create();
        partyInvites.put(inviter.getUUID(), partyInvite);

        commonMap.putAll(MapFormatters.inviterRecipientFormat(inviter, recipient));

        recipient.sendParsedMessage(MessageGrabber.grab(MessageKey.PARTY_INVITE_RECEIVED), commonMap); // Sends the party invite message cross-server (if necessary.)

        party.announce(MessageGrabber.grab(MessageKey.PARTY_INVITE_SENT), commonMap); // New parser-friendly way to send messages. Send the content without placeholders applied, pass the formatMap as a parameter, it is handled by the parser automatically.
    }

}