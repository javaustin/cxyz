package com.carrotguy69.cxyz.events.bukkit;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.cmd.Vanish;
import com.carrotguy69.cxyz.events.custom.service.EventService;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.stream.Collectors;

import static com.carrotguy69.cxyz.messages.MessageUtils.formatPlaceholders;

public class JoinEvent {

    public static void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        boolean create = !NetworkPlayer.exists(p.getUniqueId());
        NetworkPlayer np = NetworkPlayer.resolvePlayer(p.getUniqueId());

        np.updateWithPlayer(p);

        if (create) {
            np.create();
        }

        else {
            np.sync();
        }

        // Hide any vanished player from this current player
        for (NetworkPlayer vanisher : CXYZ.users.values().stream().filter(NetworkPlayer::isVanished).collect(Collectors.toList())) {
            if (!np.isVisibleTo(vanisher) && np.getPlayer() != null && vanisher.getPlayer() != null && vanisher.getUUID() != np.getUUID()) {
                np.getPlayer().hidePlayer(CXYZ.plugin, vanisher.getPlayer());
            }
        }

        // If the current player is listed as vanished, vanish them!
        if (np.isVanished()) {
            Vanish.setVanish(np, true);
            np.getPlayer().setInvisible(true);
            e.setJoinMessage("");

            MessageUtils.sendActionBar(np.getPlayer(), formatPlaceholders(MessageGrabber.grab(MessageKey.VANISH_JOIN), MapFormatters.playerFormatter(np)));

        }
        else {
            np.getPlayer().setInvisible(false);
            Vanish.setVanish(np, false);
        }

        // in case there is a chat tag or rank thing
        np.updateDisplayNames();


        com.carrotguy69.cxyz.events.custom.JoinEvent customJoinEvent = new com.carrotguy69.cxyz.events.custom.JoinEvent(np);
        EventService.dispatch(customJoinEvent);
    }

}
