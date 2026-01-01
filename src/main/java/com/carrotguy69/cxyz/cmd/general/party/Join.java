package com.carrotguy69.cxyz.cmd.general.party;

import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.classes.models.db.Party;
import com.carrotguy69.cxyz.classes.models.db.PartyInvite;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.template.MapFormatters;
import com.carrotguy69.cxyz.other.*;
import com.carrotguy69.cxyz.other.messages.MessageGrabber;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.*;


public class Join implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.party.join";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("mising-args", "player"));
            return true;
        }

        Player p  = (Player) sender;

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        join(np, args[0]);

        return true;
    }

    public void join(NetworkPlayer recipient, String inviter_name) {
        // Notable case: When a player joins a party, we don't want them to see duplicate messages (one for PARTY_JOIN, and one for PARTY_JOIN_ANNOUNCEMENT).
        // So we will send the messages first, and then actually add the player to the party.

        // Make sure recipient is not in a party.
        // Make sure inviter is in a party and the owner of that party.
        // If public: Add the recipient player and send message
        // If private: Find invite where recipient = recipient and inviter = inviter, and not expired. Then add player and send message.
        // Do not forget to sync data to the database.

        Player p = recipient.getPlayer();


        NetworkPlayer inviter = NetworkPlayer.getPlayerByUsername(inviter_name);

        if (inviter == null) {
            MessageUtils.sendParsedMessage(p, MessageKey.PARTY_ERROR_PARTY_NOT_EXIST, Map.of());
            return;
        }

        Party inviterParty = Party.getPlayerParty(inviter.getUUID());
        PartyInvite invite = PartyInvite.getLastInvite(inviter, recipient);

        if (inviterParty == null) {
            MessageUtils.sendParsedMessage(p, MessageKey.PARTY_ERROR_PARTY_NOT_EXIST, Map.of());
            return;
        }

        Map<String, Object> commonMap = MapFormatters.partyFormatter(inviterParty);

        if (recipient.isInParty()) {
            MessageUtils.sendParsedMessage(p, MessageKey.PARTY_ERROR_IN_PARTY_JOIN, commonMap);
            return;
        }

        commonMap.putAll(MapFormatters.inviterRecipientFormat(inviter, recipient));

//        if (inviterParty.isPublic()) {
//            inviterParty.announce(MessageGrabber.grab(MessageKey.PARTY_JOIN_ANNOUNCEMENT), commonMap);
//            MessageUtils.sendParsedMessage(p, MessageKey.PARTY_JOIN, commonMap);
//
//            inviterParty.addPlayer(recipient.getUUID());
//            inviterParty.sync();
//
//            parties.put(inviterParty.getOwnerUUID(), inviterParty);
//
//            if (invite != null && partyInvites.containsEntry(invite.getInviterUUID(), invite)) {
//                partyInvites.remove(invite.getInviterUUID(), invite);
//                invite.delete();
//            }
//
//            return;
//        }

        if ((invite == null || invite.getExpireTimestamp() < TimeUtils.unixTimeNow()) && !inviterParty.isPublic()) {
            MessageUtils.sendParsedMessage(p, MessageKey.PARTY_ERROR_INVITE_NOT_FOUND, commonMap);
            return;
        }

        MessageUtils.sendParsedMessage(p, MessageKey.PARTY_JOIN, commonMap);

        inviterParty.announce(MessageGrabber.grab(MessageKey.PARTY_JOIN_ANNOUNCEMENT), commonMap);


        inviterParty.addPlayer(recipient.getUUID());
        inviterParty.sync();

        parties.put(inviterParty.getOwnerUUID(), inviterParty);


        if (invite != null) {
            partyInvites.remove(invite.getInviterUUID(), invite);
            invite.delete();
        }

    }


}