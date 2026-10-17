package com.carrotguy69.cxyz.events.bukkit;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Punishment;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandPreprocessEvent {
    public static void onCommand(PlayerCommandPreprocessEvent e) {

        String commandString = e.getMessage();

        NetworkPlayer np = NetworkPlayer.resolvePlayer(e.getPlayer().getUniqueId());

        commandString = commandString.substring(1); // Remove leading "/"

        for (String restrictedCommand : CXYZ.muteRestrictions) {
            if (commandString.startsWith(restrictedCommand) && np.isMuted()) {
                Punishment mute = Punishment.getActivePunishment(np, Punishment.PunishmentType.MUTE);

                MessageUtils.sendParsedMessage(e.getPlayer(), MessageKey.PUNISHMENT_MUTE_PLAYER_MESSAGE, MapFormatters.punishmentFormatter(e.getPlayer(), mute));
                e.setCancelled(true);
                return;
            }
        }
    }
}
