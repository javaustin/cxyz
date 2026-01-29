package com.carrotguy69.cxyz.cmd.general.party;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Party;
import com.carrotguy69.cxyz.models.db.PartyInvite;
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

import java.util.Map;
import java.util.Objects;

import static com.carrotguy69.cxyz.CXYZ.*;


public class PartyJoin implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        /*
        SYNTAX:
            /party join <player>
            /party join Notch
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.party.join";

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


        if (Objects.equals(inviter.getUUID(), recipient.getUUID())) {
            MessageUtils.sendParsedMessage(inviter.getPlayer(), MessageKey.PLAYER_IS_SELF, Map.of());
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