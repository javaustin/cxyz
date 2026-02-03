package com.carrotguy69.cxyz.events;

import com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Party;
import com.carrotguy69.cxyz.models.db.PartyExpire;
import com.carrotguy69.cxyz.models.db.Punishment;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.other.utils.TimeUtils;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.messages.MessageKey;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Objects;

import static com.carrotguy69.cxyz.CXYZ.*;
import static com.carrotguy69.cxyz.messages.MessageParser.unescape;

public class JoinEvent {

    public static void onJoin(Player p) {
        NetworkPlayer np;
        boolean create = false;

        if (!isInitialized()) {
            p.kickPlayer("Server is not finished initializing. Please wait!");
            return;
        }

        try {
            np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());
        }
        catch (Exception e) {
            np = new NetworkPlayer().createFromPlayer(p);
            create = true;
        }

        if (np.isBanned()) {
            Punishment ban = Punishment.getActivePunishment(np, Punishment.PunishmentType.BAN);
            Map<String, Object> map = MapFormatters.punishmentFormatter(np.getPlayer(), ban);

            if (ban.isPermanent())
                p.kickPlayer(String.join("\n", unescape(MessageGrabber.grab(MessageKey.PUNISHMENT_BAN_PLAYER_MESSAGE_PERMANENT, map))));
            else
                p.kickPlayer(String.join("\n", unescape(MessageGrabber.grab(MessageKey.PUNISHMENT_BAN_PLAYER_MESSAGE, map))));

            return;
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
        np.setServer(this_server);
        np.setLastOnline(TimeUtils.unixTimeNow());
        np.setLastJoin(TimeUtils.unixTimeNow());
        np.setLastIP(p.getAddress() != null ? p.getAddress().getAddress().getHostAddress() : null);
        np.setOnline(true);

        if (create) {
            np.create();
        }
        else {
            np.sync();
        }

        if (!np.isOnline()) {
            Logger.severe("NetworkPlayer " + np.getUsername() + " is offline apparently but they just joined!?");
            Logger.log(np.toString());

            NetworkPlayer finalNp = np;
            new BukkitRunnable() {
                @Override
                public void run() {
                    Logger.log("NetworkPlayer joined (after 10s): " + NetworkPlayer.getPlayerByUUID(finalNp.getUUID()).toString());
                }
            }.runTaskLater(plugin, 200L);
        }

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

            np.equipCosmetic(cosmetic);
        }
    }


}
