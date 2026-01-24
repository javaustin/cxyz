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

import java.util.*;

public class PartyList implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /party list
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.party.list";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        Player p = (Player) sender;

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        list(np);

        return true;
    }

    public void list(NetworkPlayer np) {
        Player p = np.getPlayer();

        Party party = Party.getPlayerParty(np.getUUID());

        if (party == null) {
            MessageUtils.sendParsedMessage(p, MessageKey.PARTY_ERROR_NOT_IN_PARTY, Map.of());
            return;
        }

        // The party owner by design is separate, so they aren't really in the players list. But for good 'ol, UI's sake, it makes more sense if they are, so we'll fake it.
        List<NetworkPlayer> effectivePlayers = new ArrayList<>();
        for (String uuid : party.getPlayers()){
            effectivePlayers.add(NetworkPlayer.getPlayerByUUID(UUID.fromString(uuid)));
        }
        effectivePlayers.add(NetworkPlayer.getPlayerByUUID(party.getOwnerUUID()));

        // Get the format and delimiter from messages.yml
        Map<String, Object> commonMap = MapFormatters.partyFormatter(party);
        String format = MessageGrabber.grab(MessageKey.PARTY_LIST_PLAYER_FORMAT);
        String delimiter = MessageGrabber.grab(MessageKey.PARTY_LIST_PLAYER_SEPARATOR);


        MapFormatters.ListFormatter formatter =  MapFormatters.playerListFormatter(effectivePlayers, format, delimiter);
        commonMap.putAll(formatter.getFormatMap());


        String unparsed = MessageGrabber.grab(MessageKey.PARTY_LIST);

        unparsed = unparsed.replace("{players}", !effectivePlayers.isEmpty() ? formatter.getText() : "None"); // formatter.getText() is the formatter built list of players with the given player format and player delimiter.

        MessageUtils.sendParsedMessage(p, unparsed, commonMap);
        return;
    }

}
