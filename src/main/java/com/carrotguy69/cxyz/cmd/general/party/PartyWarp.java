package com.carrotguy69.cxyz.cmd.general.party;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.config.GameServer;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Party;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.other.utils.TimeUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.carrotguy69.cxyz.CXYZ.plugin;
import static com.carrotguy69.cxyz.CXYZ.taskIDs;

public class PartyWarp implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /party warp [server]
            /party warp HUB_1
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.party.warp";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }


        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        Player p = (Player) sender;

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        if (args.length > 0) {
            warp(np, GameServer.getServerFromName(args[0]));
            return true;
        }

        warp(np, null);

        return true;
    }

    public void warp(NetworkPlayer owner, GameServer destinationServer) {
        if (destinationServer == null) {
            destinationServer = owner.getServer();
        }

        Party party = Party.getPlayerParty(owner.getUUID());

        if (party == null) {
            MessageUtils.sendParsedMessage(owner.getPlayer(), MessageKey.PARTY_ERROR_PLAYER_NOT_IN_PARTY, Map.of());
            return;
        }

        if (!Objects.equals(party.getOwnerUUID(), owner.getUUID())) {
            MessageUtils.sendParsedMessage(owner.getPlayer(), MessageKey.PARTY_ERROR_LEADER_ONLY, MapFormatters.partyFormatter(party));
            return;
        }

        // 1. Ensure owner is in a party and is the party leader.
        // 2. Loop through players in the party. Of players that are not already on the destination server, warp them to that server.
        // 3. Create a task that will incrementally check if the player is on the destination server. Remove them from the list and notify them.
        // 4. Cancel the task when the list is empty.

//        party.addPlayer(owner.getUUID()); // don't worry, we will not save this object in memory or sync it. is this nigga stupid this is exactly what happened?
        for (String uuid : party.getPlayers()) {
            NetworkPlayer partyPlayer = NetworkPlayer.getPlayerByUUID(UUID.fromString(uuid));

            if (!partyPlayer.isOnline() ) {
                MessageUtils.sendParsedMessage(owner.getPlayer(), MessageKey.PARTY_ERROR_WARP_FAILED, MapFormatters.partyFormatter(party));
                return;
            }

            partyPlayer.warp(destinationServer);
        }

        owner.warp(destinationServer);


        Set<String> unArrived = new HashSet<>(party.getPlayers()); // Sounds like an Asian TikTok influencer
        unArrived.add(owner.getUUID().toString());

        GameServer finalDestinationServer = destinationServer;
        long timestamp = TimeUtils.unixTimeNow();

        Map<String, Object> commonMap = MapFormatters.partyFormatter(party);
        commonMap.put("server", destinationServer.getName());

        int id = new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<String> iterator = unArrived.iterator();
                while (iterator.hasNext()) {
                    String uuid = iterator.next();
                    NetworkPlayer partyPlayer = NetworkPlayer.getPlayerByUUID(UUID.fromString(uuid));

                    if (Objects.equals(partyPlayer.getServer().getName(), finalDestinationServer.getName())) {
                        partyPlayer.sendParsedMessage(MessageGrabber.grab(MessageKey.PARTY_WARP_ANNOUNCEMENT), commonMap);
                        iterator.remove();
                    }
                }

                // Cancel the task if everyone has arrived or timeout reached
                if (unArrived.isEmpty() || TimeUtils.unixTimeNow() > timestamp + 10) {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 5L).getTaskId();
        taskIDs.add(id);
    }


}
