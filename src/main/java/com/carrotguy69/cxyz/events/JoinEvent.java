package com.carrotguy69.cxyz.events;

import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.classes.models.db.PartyExpire;
import com.carrotguy69.cxyz.classes.models.db.Punishment;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.template.MapFormatters;
import com.carrotguy69.cxyz.other.TimeUtils;
import com.carrotguy69.cxyz.other.messages.MessageGrabber;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Objects;

import static com.carrotguy69.cxyz.CXYZ.*;
import static com.carrotguy69.cxyz.other.messages.MessageParser.unescape;

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

        np.getPlayer().setDisplayName(np.getRank().getColor() + np.getDisplayName());
        np.getPlayer().setPlayerListName(np.getDisplayName());


        String rank = "default";
        if (np.getRank() != null) { // If a rank somehow became invalid (config updated)
            rank = np.getRank().getName();
        }
        p.addAttachment(instance).setPermission("cxyz.rank." + rank, true);
        // update to support LuckPerms - p.addAttachment() is a temporary permission. We need to send it to LuckPerms either through API or simple commands.

        PartyExpire expire = partyExpires.get(p.getUniqueId());
        if (expire != null) {
            expire.delete();
        }

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
            }.runTaskLater(instance, 200L);
        }

    }


}
