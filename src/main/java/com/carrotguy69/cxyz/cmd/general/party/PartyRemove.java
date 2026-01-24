package com.carrotguy69.cxyz.cmd.general.party;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Party;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
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
import java.util.Objects;

public class PartyRemove implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /party remove <player>
            /party remove Steve
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.party.remove";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
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

        Player p  = (Player) sender;

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());
        NetworkPlayer target = NetworkPlayer.getPlayerByUsername(args[0]);

        if (target == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", args[0]));
            return true;
        }

        remove(np, target);

        return true;
    }

    public static void remove(NetworkPlayer sender, NetworkPlayer target) {
        Party party = Party.getPlayerParty(sender.getUUID());

        // make sure sender cant kick themselves

        if (Objects.equals(sender.getUUID(), target.getUUID())) {
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.PLAYER_IS_SELF, Map.of());
            return;
        }

        if (party == null) {
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.PARTY_ERROR_NOT_IN_PARTY, Map.of());
            return;
        }

        Map<String, Object> commonMap = MapFormatters.partyFormatter(party);

        if (!Objects.equals(sender.getUUID(), party.getOwnerUUID())) {
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.PARTY_ERROR_LEADER_ONLY, commonMap);
            return;
        }

        if (party.getPlayers() == null || !party.getPlayers().contains(target.getUUID().toString())) {
            commonMap.putAll(MapFormatters.playerFormatter(target));
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.PARTY_ERROR_PLAYER_NOT_IN_PARTY, commonMap);
            return;
        }

        party.removePlayer(target.getUUID());


        commonMap.putAll(MapFormatters.inviterRecipientFormat(sender, target));

        party.announce(MessageGrabber.grab(MessageKey.PARTY_REMOVE_ANNOUNCEMENT), commonMap);
        MessageUtils.sendParsedMessage(target.getPlayer(), MessageKey.PARTY_REMOVE, commonMap);

        party.sync();


    }
}
