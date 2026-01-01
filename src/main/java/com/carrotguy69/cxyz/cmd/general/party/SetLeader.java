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
import java.util.Objects;

import static com.carrotguy69.cxyz.CXYZ.gson;
import static com.carrotguy69.cxyz.CXYZ.parties;

public class SetLeader implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.party.setleader";

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

        if (!target.isOnline() || !target.isVisibleTo(np)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_IS_OFFLINE, MapFormatters.playerFormatter(target));
            return true;
        }

        setLeader(np, target);

        return true;
    }

    public static void setLeader(NetworkPlayer sender, NetworkPlayer target) {
        Party party = Party.getPlayerParty(sender.getUUID());

        if (Objects.equals(sender.getUUID(), target.getUUID())) {
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.PLAYER_IS_SELF, Map.of());
            return;
        }

        if (party == null) {
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.PARTY_ERROR_NOT_IN_PARTY, Map.of());
            return;
        }

        if (!Objects.equals(sender.getUUID(), party.getOwnerUUID())) {
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.PARTY_ERROR_LEADER_ONLY, MapFormatters.partyFormatter(party));
            return;
        }

        if (party.getPlayers() == null || !party.getPlayers().contains(target.getUUID().toString())) {
            Map<String, Object> map = MapFormatters.partyFormatter(party);
            map.putAll(MapFormatters.playerFormatter(target));
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.PARTY_ERROR_PLAYER_NOT_IN_PARTY, map);
            return;
        }

        if (!target.isOnline() || (target.isOnline() && !target.isVisibleTo(sender))) {
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.PLAYER_IS_OFFLINE, MapFormatters.playerFormatter(target));
            return;
        }

        parties.remove(sender.getUUID()); // Remove the party from the global map. Don't worry, we still have the reference.
        party.delete(); // Request the API to delete the current party record.
        // We cannot change the UUID because the database wouldn't know what to change.


        Party newParty = new Party(target.getUUID().toString(), gson.toJson(party.getPlayers()), party.isPublic() ? 1 : 0);

        party.removePlayer(target.getUUID()); // Remove the new owner from the player list.
        newParty.removePlayer(target.getUUID()); // Remove the new owner from the player list.

        newParty.addPlayer(party.getOwnerUUID()); // Add the previous party owner to the player list.

        parties.put(target.getUUID(), newParty);

        Map<String, Object> commonMap = MapFormatters.inviterRecipientFormat(sender, target);
        commonMap.putAll(MapFormatters.partyFormatter(party));

        newParty.announce(MessageGrabber.grab(MessageKey.PARTY_TRANSFER), commonMap);

        newParty.create();
    }
}
