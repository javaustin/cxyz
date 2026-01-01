package com.carrotguy69.cxyz.other;

import com.carrotguy69.cxyz.classes.http.Request;
import com.carrotguy69.cxyz.classes.http.RequestType;
import com.carrotguy69.cxyz.classes.models.config.Announcement;
import com.carrotguy69.cxyz.classes.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.classes.models.db.Party;
import com.carrotguy69.cxyz.classes.models.db.PartyExpire;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import com.carrotguy69.cxyz.template.MapFormatters;
import com.carrotguy69.cxyz.other.messages.MessageGrabber;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Tasks {

    // We will probably move a lot of this to the backend API.

    public static void fixOnlinePlayers() {
        int id = new BukkitRunnable() {public void run() {
            for (Player p : Bukkit.getOnlinePlayers()) {
                NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

                if (!np.isOnline() && p.isOnline()) {
                    Logger.warning(String.format("NetworkPlayer %s is offline but their Player is really online!", np.getUsername()));
                    np.setOnline(true);
                    np.sync();
                }
            }

        }}.runTaskTimer(instance, 100L, 10 * 20).getTaskId();

        taskIDs.add(id);
    }

    public static void handlePartyExpires() {
        int id = new BukkitRunnable() {public void run() {
            for (PartyExpire expire : partyExpires.values()) {
                if (expire.getTimestamp() > TimeUtils.unixTimeNow()) {
                    NetworkPlayer np = NetworkPlayer.getPlayerByUUID(expire.getUUID());

                    if (!np.isOnline()) {
                        Party party = Party.getPlayerParty(np.getUUID());

                        if (party != null && party.getPlayers() != null) {

                            if (party.getOwnerUUID() == np.getUUID()) { // If the owner is offline and kind of abandoned their party, then it will auto disband in 5 minutes
                                party.announce(MessageGrabber.grab(MessageKey.PARTY_DISBAND), MapFormatters.partyFormatter(party));
                                parties.remove(party.getOwnerUUID(), party);
                                party.delete();
                            }

                            else {
                                Map<String, Object> map = MapFormatters.playerFormatter(np);
                                map.putAll(MapFormatters.partyFormatter(party));

                                party.removePlayer(np.getUUID());

                                party.announce(MessageGrabber.grab(MessageKey.PARTY_REMOVE_INACTIVE_ANNOUNCEMENT), map); // This is not true! The player does not actually get removed, and this behavior is unpredictable at runtime.


                                party.sync();
                            }
                        }

                        partyExpires.remove(np.getUUID(), expire);
                        expire.delete();
                    }

                    else
                        Logger.warning(String.format("In cancelling party expires, Player %s was expected to be offline according to the expire map, but was found online! Ignoring!", np.getUsername()));
                }
            }


        }}.runTaskTimer(instance, 0L, 10 * 20).getTaskId();

        taskIDs.add(id);
    }

    public static void fixIgnorables() {
        // In config.yml (in chat), it is possible for a channel to be ignorable (the ability for individual players to ignore a channel).
        // If this value is set to false, the channel should not be ignorable, but players may have already added it to their ignore list.
        // This task removes these unignorable channels from players ignore lists through a backend SQL query, and on the front end.


        int id = new BukkitRunnable() {public void run() {
            for (BaseChannel channel : BaseChannel.getAllChannels()) {
                if (!channel.isIgnorable()) {
                    String query = String.format(
                            "UPDATE users\n" +
                                    "SET muted_channels = (\n" +
                                    "\tSELECT json_group_array(value)\n" +
                                    "\tFROM json_each(users.muted_channels)\n" +
                                    "\tWHERE value != '%s'\n" +
                                    ");",
                            channel.getName()
                    );

                    new Request(RequestType.POST, api_endpoint + "/sql", gson.toJson(Map.of(
                            "query", query,
                            "table", "users")
                    )).send();
                }
            }
        }}.runTaskTimer(instance, 0L, 120 * 20).getTaskId();

        taskIDs.add(id);


    }

    public static void createAnnouncementTasks() {
        for (Announcement annc : Announcement.loadAnnouncements()) {

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
                }.runTaskLater(instance, annc.getDelay()).getTaskId();

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
                }.runTaskTimer(instance, annc.getDelay(), annc.getInterval()).getTaskId();

            }
            taskIDs.add(id);


        }
    }


}
