package com.carrotguy69.cxyz.other;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.config.Announcement;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Party;
import com.carrotguy69.cxyz.models.db.PartyExpire;
import com.carrotguy69.cxyz.models.db.PartyInvite;
import com.carrotguy69.cxyz.other.utils.TimeUtils;
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

                NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

                if (!np.isOnline() && p.isOnline()) {
                    Logger.warning(String.format("NetworkPlayer %s is offline but their Player is really online!", np.getUsername()));
                    np.setOnline(true);
                    np.sync();
                }
            }

        }}.runTaskTimer(plugin, 100L, 10 * 20).getTaskId();

        taskIDs.add(id);
    }

    public static void handlePartyExpires() {
        if (partyAutoKickAfter < 0)
            return;

        int id = new BukkitRunnable() {public void run() {


            for (PartyExpire expire : partyExpires.values()) {
                if (TimeUtils.unixTimeNow() > expire.getTimestamp()) {
                    NetworkPlayer np = NetworkPlayer.getPlayerByUUID(expire.getUUID());

                    if (!np.isOnline()) {
                        Party party = Party.getPlayerParty(np.getUUID());

                        if (party != null && party.getPlayers() != null) {

                            if (party.getOwnerUUID().equals(np.getUUID())) { // If the owner is offline and kind of abandoned their party, then it will auto disband.
                                party.announce(MessageGrabber.grab(MessageKey.PARTY_DISBAND), MapFormatters.partyFormatter(party));
                                party.delete();
                                parties.remove(party.getOwnerUUID(), party);
                            }

                            else {
                                Map<String, Object> map = MapFormatters.playerFormatter(np);
                                map.putAll(MapFormatters.partyFormatter(party));

                                party.removePlayer(np.getUUID());

                                party.announce(MessageGrabber.grab(MessageKey.PARTY_REMOVE_INACTIVE_ANNOUNCEMENT), map); // This is not true! The player does not actually get removed, and this behavior is unpredictable at runtime.

                                party.sync();

                                parties.put(party.getOwnerUUID(), party);
                            }
                        }

                        partyExpires.remove(np.getUUID(), expire);
                        expire.delete();
                    }

                    else
                        Logger.warning(String.format("In cancelling party expires, Player %s was expected to be offline according to the expire map, but was found online! Ignoring!", np.getUsername()));
                }
            }


        }}.runTaskTimer(plugin, 0L, 10 * 20).getTaskId();

        taskIDs.add(id);
    }

    public static void handlePartyInvites() {
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
                    party.announce(MessageGrabber.grab(MessageKey.PARTY_INVITE_EXPIRED), MapFormatters.inviterRecipientFormat(invite.getInviter(), invite.getRecipient()));
                }

                partyInvites.remove(invite.getInviterUUID(), invite);
                invite.delete();
            }


        }}.runTaskTimer(plugin, 0L, 60 * 20).getTaskId();

        taskIDs.add(id);
    }
//
//    public static void fixChannels() {
//        // In config.yml (in chat), it is possible for a channel to be ignorable (the ability for individual players to ignore a channel).
//        // If this value is set to false, the channel should not be ignorable, but players may have already added it to their ignore list.
//        // This task removes these unignorable channels from players ignore lists through a backend SQL query, and on the front end.
//
//
//        int id = new BukkitRunnable() {public void run() {
//            for (BaseChannel channel : BaseChannel.getAllChannels()) {
//                if (!channel.isIgnorable()) {
//                    String query = String.format(
//                            "UPDATE users\n" +
//                                    "SET muted_channels = (\n" +
//                                    "\tSELECT json_group_array(value)\n" +
//                                    "\tFROM json_each(users.muted_channels)\n" +
//                                    "\tWHERE value != '%s'\n" +
//                                    ");",
//                            channel.getName()
//                    );
//
//                    new Request(RequestType.POST, api_endpoint + "/sql", gson.toJson(Map.of(
//                            "query", query,
//                            "table", "users")
//                    )).send();
//                }
//            }
//        }}.runTaskTimer(instance, 0L, 600 * 20).getTaskId();
//
//        taskIDs.add(id);
//    }

    public static void createAnnouncementTasks() {
        for (Announcement annc : Announcement.getAnnouncements()) {

            int id;
            if (annc.getInterval() == 0 || annc.getInterval() == -1) {
                // Announcement does not repeat, use a runTaskLater function.

                id = new BukkitRunnable() {
                    public void run() {

                        for (NetworkPlayer np : users.values()) {
                            if (annc.getServers().contains(np.getServer().getName().toUpperCase()) && np.isOnline() && !np.isMutingChannel("announcement")) {
                                np.sendParsedMessage(annc.getContent(), MapFormatters.playerFormatter(np));
                            }
                        }

                        if (annc.sendsToConsole())
                            MessageUtils.sendParsedMessage(Bukkit.getConsoleSender(), annc.getContent(), Map.of());


                    }
                }.runTaskLater(plugin, annc.getDelay()).getTaskId();

            }

            else {

                id = new BukkitRunnable() {
                    public void run() {

                        for (NetworkPlayer np : users.values()) {
                            if (annc.getServers().contains(np.getServer().getName().toUpperCase()) && np.isOnline() && !np.isMutingChannel("announcement")) {
                                np.sendParsedMessage(annc.getContent(), MapFormatters.playerFormatter(np));
                            }
                        }

                        if (annc.sendsToConsole())
                            MessageUtils.sendParsedMessage(Bukkit.getConsoleSender(), annc.getContent(), Map.of());


                    }
                }.runTaskTimer(plugin, annc.getDelay(), annc.getInterval()).getTaskId();

            }
            taskIDs.add(id);


        }
    }

    public static void updateLastOnlineValues() {
        // Add a task that updates last online value every few minutes (maybe 2 minutes?)

        new BukkitRunnable() {
            public void run() {

                for (NetworkPlayer np : users.values()) {
                    if (np.isOnline() && np.getPlayer() != null && np.getPlayer().isOnline()) {
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
                NetworkPlayer owner = NetworkPlayer.getPlayerByUUID(party.getOwnerUUID());
                boolean allOffline = true;

                if (owner.isOnline()) {
                    continue;
                }

                for (String uuidString : party.getPlayers()) {
                    NetworkPlayer partyPlayer = NetworkPlayer.getPlayerByUUID(UUID.fromString(uuidString));

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


}
