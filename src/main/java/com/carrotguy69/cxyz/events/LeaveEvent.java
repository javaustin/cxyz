package com.carrotguy69.cxyz.events;

import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.classes.models.db.PartyExpire;
import com.carrotguy69.cxyz.other.TimeUtils;
import org.bukkit.entity.Player;

import static com.carrotguy69.cxyz.CXYZ.partyInvitesExpireAfter;
import static com.carrotguy69.cxyz.CXYZ.users;

public class LeaveEvent {

    public static void onLeave(Player p) {

        if (users.isEmpty()) {
            return;
        }

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        long currentPlaytime = np.getPlaytime();
        long playtimeSession = TimeUtils.unixTimeNow() - np.getLastJoin();

        if (np.isInParty()) {
            PartyExpire expire = new PartyExpire(p.getUniqueId().toString(), TimeUtils.unixTimeNow() + partyInvitesExpireAfter);
            expire.create();
        }

        np.setPlaytime(currentPlaytime + playtimeSession);
        np.setOnline(false);
        np.setLastOnline(TimeUtils.unixTimeNow());

        np.sync();


        // update to send out a partyExpire
    }

}
