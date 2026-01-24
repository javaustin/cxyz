package com.carrotguy69.cxyz.events;

import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Party;
import com.carrotguy69.cxyz.models.db.PartyExpire;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.other.utils.TimeUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.*;

public class LeaveEvent {

    public static void onLeave(Player p) {

        if (users.isEmpty()) {
            return;
        }

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        long currentPlaytime = np.getPlaytime();
        long playtimeSession = TimeUtils.unixTimeNow() - np.getLastJoin();


        np.setPlaytime(currentPlaytime + playtimeSession);
        np.setOnline(false);
        np.setLastOnline(TimeUtils.unixTimeNow());

        np.sync();

        // Create a party expire (task). Only if the plugin is enabled still (not shutting down)
        if (plugin.isEnabled()) {
            new BukkitRunnable() {
                public void run() {

                    Logger.log("isInParty: " + np.isInParty());
                    Logger.log("isOnline: " + np.isOnline());
                    Logger.log("partyAutoKickAfter (? > 0): " + partyAutoKickAfter);

                    if (np.isInParty() && !np.isOnline() && partyAutoKickAfter > 0) {
                        PartyExpire expire = new PartyExpire(np.getUUID().toString(), TimeUtils.unixTimeNow() + partyAutoKickAfter);
                        partyExpires.put(np.getUUID(), expire);
                        expire.create();

                        Party party = Party.getPlayerParty(np.getUUID());

                        if (party == null) {
                            throw new RuntimeException("Party.getPlayerParty(np.getUUID()) should not be null if np.isInParty() is true.");
                        }

                        Map<String, Object> commonMap = MapFormatters.partyFormatter(party);
                        commonMap.putAll(MapFormatters.playerFormatter(np));

                        party.announce(MessageGrabber.grab(MessageKey.PARTY_PLAYER_DISCONNECT), commonMap);
                    }
                }
            }
                    .runTaskLater(plugin, 5L);
        }

    }

}
