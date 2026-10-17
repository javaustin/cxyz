package com.carrotguy69.cxyz.other;

import com.carrotguy69.cxyz.http.Request;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.config.Announcement;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Party;
import com.carrotguy69.cxyz.models.db.PartyInvite;
import com.carrotguy69.cxyz.utils.JsonConverters;
import com.carrotguy69.cxyz.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Tasks {

    // We will probably move a lot of this to the backend API.

    public static void fixOnlinePlayers() {
        int id = new BukkitRunnable() {public void run() {
            for (Player p : Bukkit.getOnlinePlayers()) {

                if (p == null || !p.isOnline())
                    continue;

                NetworkPlayer np = NetworkPlayer.resolvePlayer(p.getUniqueId());

                if (!np.isOnline() && p.isOnline()) {
                    Logger.warning(String.format("NetworkPlayer %s is offline but their Player is really online!", np.getUsername()));
                    np.setOnline(true);
                    np.sync();
                }
            }

        }}.runTaskTimer(plugin, 100L, 10 * 20).getTaskId();

        taskIDs.add(id);
    }

    public static void clearPartyInvites() {
        if (partyInvitesExpireAfter < 0)
            return;

        int id = new BukkitRunnable() {public void run() {

            List<PartyInvite> toDelete = new ArrayList<>();

            for (Map.Entry<UUID, PartyInvite> entry : partyInvites.entries()) {
                if (!(entry.getValue().getExpireTimestamp() < TimeUtils.unixTimeNow())) {
                    continue;
                }

                // Delete them!
                toDelete.add(entry.getValue());
            }

            for (PartyInvite invite : toDelete) {
                Party party = Party.getPlayerParty(invite.getInviterUUID());

                if (party != null) {
                    party.announce(MessageGrabber.grab(MessageKey.PARTY_INVITE_EXPIRED_ANNOUNCEMENT), MapFormatters.inviterRecipientFormat(invite.getInviter(), invite.getRecipient()));
                    invite.getInviter().sendMessage(MessageGrabber.grab(MessageKey.PARTY_INVITE_EXPIRED), MapFormatters.playerFormatter(invite.getRecipient()));
                }

                partyInvites.remove(invite.getInviterUUID(), invite);
                invite.delete();
            }


        }}.runTaskTimer(plugin, 0L, 60 * 20).getTaskId();

        taskIDs.add(id);
    }

    public static void createAnnouncementTasks() {
        for (Announcement announcement : announcements) {

            if (!announcement.isEnabled()) {
                continue;
            }

            int id;
            if (announcement.getInterval() == 0 || announcement.getInterval() == -1) {
                // Announcement does not repeat, use a runTaskLater function.

                id = new BukkitRunnable() {
                    public void run() {
                        announcement.send();
                    }
                }.runTaskLater(plugin, announcement.getDelay() * 20).getTaskId();

            }

            else {

                id = new BukkitRunnable() {
                    public void run() {
                        announcement.send();
                    }
                }.runTaskTimer(plugin, announcement.getDelay() * 20, announcement.getInterval() * 20).getTaskId();

            }
            taskIDs.add(id);
        }
    }

    public static void updateLastOnlineValues() {
        // Add a task that updates last online value every few minutes (maybe 2 minutes?)

        new BukkitRunnable() {
            public void run() {

                for (NetworkPlayer np : users.values()) {
                    if (np.isOnline() && np.getPlayer() != null && np.getPlayer() != null) {
                        np.setLastOnline(TimeUtils.unixTimeNow());
                        np.sync();
                    }
                }

            }
        }.runTaskTimer(plugin, 10 * 20L, 120 * 20L);
    }

    public static void deleteOfflineParties() {
        new BukkitRunnable() { public void run() {

            for (Party party : parties.values()) {
                NetworkPlayer owner = NetworkPlayer.resolvePlayer(party.getOwnerUUID());
                boolean allOffline = true;

                if (owner.isOnline()) {
                    continue;
                }

                for (String uuidString : party.getPlayers()) {
                    NetworkPlayer partyPlayer = NetworkPlayer.resolvePlayer(UUID.fromString(uuidString));

                    if (partyPlayer.isOnline()) {
                        allOffline = false;
                        break;
                    }

                }

                if (allOffline) {
                    parties.remove(party.getOwnerUUID(), party);
                    party.delete();
                }

            }

        }}.runTaskTimer(plugin, 0, 30 * 20L);
    }

    public static void runPunishmentSeq() {
        new BukkitRunnable() { public void run() {
           punishmentSeq();
        }}.runTaskTimer(plugin, 0, 60 * 20L);
    }

    public static void punishmentSeq() {
        Request.getRequest(apiEndpoint + "/seq/punishments").thenAccept(result -> {
            if (result.statusCode != 200)
                return;

            Bukkit.getScheduler().runTask(plugin, () -> {
                punishmentSeq = (int) Math.round((double) JsonConverters.toMap(result.responseBody).get("seq"));
            });

        });
    }

    public static void runServerListQuery() {
        new BukkitRunnable() { public void run() {
            serverListQuery();
        }}.runTaskTimer(plugin, 600, 60 * 20L);
    }

    public static void serverListQuery() {
        Request.getRequest(apiEndpoint + "/servers").thenAccept(result -> {
            if (result.statusCode != 200)
                return;

            Bukkit.getScheduler().runTask(plugin, () -> {
                servers = (List<String>) JsonConverters.toMap(result.responseBody).get("servers");
            });

        });

    }

}
