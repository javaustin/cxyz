package com.carrotguy69.cxyz.events.bukkit;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent {

    public static void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        boolean create = !NetworkPlayer.exists(p.getUniqueId());
        NetworkPlayer np = NetworkPlayer.resolvePlayer(p.getUniqueId());

        if (create)
            np.create();

        else
            np.sync();

    }

}
