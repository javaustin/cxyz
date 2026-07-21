package com.carrotguy69.cxyz.events.bukkit;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.utils.TimeUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import static com.carrotguy69.cxyz.CXYZ.*;

public class LeaveEvent {

    public static void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (users.isEmpty()) {
            return;
        }

        NetworkPlayer np = NetworkPlayer.resolvePlayer(p.getUniqueId());


        long currentPlaytime = np.getPlaytime();
        long playtimeSession = TimeUtils.unixTimeNow() - np.getLastJoin();


        np.setPlaytime(currentPlaytime + playtimeSession);
        np.setOnline(false);
        np.setLastOnline(TimeUtils.unixTimeNow());

        np.sync();

        np.unEquipActiveCosmetics();

        if (np.isVanished()) {
            e.setQuitMessage("");
        }

    }

}
