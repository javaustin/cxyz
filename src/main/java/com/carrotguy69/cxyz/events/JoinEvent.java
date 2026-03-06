package com.carrotguy69.cxyz.events;

import com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Party;
import com.carrotguy69.cxyz.models.db.PartyExpire;
import com.carrotguy69.cxyz.models.db.Punishment;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.utils.TimeUtils;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.messages.MessageKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.Objects;

import static com.carrotguy69.cxyz.CXYZ.*;

public class JoinEvent {

    public static void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        NetworkPlayer np;
        boolean create = false;

        try {
            np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());
        }
        catch (Exception ex) {
            np = new NetworkPlayer().createFromPlayer(p);
            create = true;
        }

        // Update missing values
        if (!Objects.equals(np.getUsername(), p.getName())) {
            np.setUsername(p.getName());
        }

        if (np.getNickname() != null) {
            np.getPlayer().setDisplayName(np.getNickname());
        }

        np.getPlayer().setDisplayName(np.getTopRank().getColor() + np.getDisplayName());
        np.getPlayer().setPlayerListName(np.getDisplayName());


        String rank = "default";
        if (np.getTopRank() != null) { // If a rank somehow became invalid (config updated)
            rank = np.getTopRank().getName();
        }
        p.addAttachment(plugin).setPermission("cxyz.rank." + rank, true);
        // update to support LuckPerms - p.addAttachment() is a temporary permission. We need to send it to LuckPerms either through API or simple commands.


        // Updates outdated values - set to new ones
        np.setServer(thisServer);
        np.setLastOnline(TimeUtils.unixTimeNow());
        np.setLastJoin(TimeUtils.unixTimeNow());
        np.setLastIP(p.getAddress() != null ? p.getAddress().getAddress().getHostAddress() : null);
        np.setOnline(true);


        // remove party expire if it exists
        PartyExpire expire = partyExpires.get(np.getUUID());
        if (expire != null) {
            expire.delete();
            partyExpires.remove(expire.getUUID());

            if (np.isInParty() && np.isOnline()) {

                Party party = Party.getPlayerParty(np.getUUID());

                if (party != null) {

                    Map<String, Object> commonMap = MapFormatters.partyFormatter(party);
                    commonMap.putAll(MapFormatters.playerFormatter(np));

                    party.announce(MessageGrabber.grab(MessageKey.PARTY_PLAYER_RECONNECT), commonMap);
                }
                else {
                    Logger.warning("party should not be null with np.isInParty() check. Ignoring because onJoin is a critical process.");
                }

            }
        }

        // Apply equipped cosmetics if the server allows
        for (Cosmetic cosmetic : np.getEquippedCosmetics()) {
            if (!(cosmetic.isEnabled() && enabledCosmeticTypes.contains(cosmetic.getType()))) {
                continue;
            }

            if (cosmetic.getType() == Cosmetic.CosmeticType.RANK_PLATE && cosmetic.getRequiredRank().getHierarchy() > np.getTopRank().getHierarchy()) {
                continue;
            }

            np.equipCosmetic(cosmetic);
        }

        if (create) {
            np.create();
        }
        else {
            np.sync();
        }
    }


}
