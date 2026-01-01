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


public class Leave implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.party.leave";

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

        leave(np);

        return true;
    }

    public void leave(NetworkPlayer np) {
        Player p = np.getPlayer();


        Party party = Party.getPlayerParty(np.getUUID());

        if (party == null) {
            MessageUtils.sendParsedMessage(p, MessageKey.PARTY_ERROR_NOT_IN_PARTY, Map.of());
            return;
        }

        if (Party.isPartyOwner(np.getUUID())) {
            MessageUtils.sendParsedMessage(p, MessageKey.PARTY_ERROR_MUST_DISBAND, MapFormatters.partyFormatter(party));
            return;
        }

        party.removePlayer(np.getUUID());
        party.sync();

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);
        commonMap.putAll(MapFormatters.partyFormatter(party));

        party.announce(MessageGrabber.grab(MessageKey.PARTY_LEFT_ANNOUNCEMENT), commonMap);

        MessageUtils.sendParsedMessage(p, MessageKey.PARTY_LEFT, commonMap);

    }
}
