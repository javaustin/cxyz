package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.classes.models.config.GameServer;
import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Party implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> subcommands = List.of("chat", "create", "delete", "disband", "invite", "join", "kick", "list", "private", "leave", "public", "remove", "setleader", "warp");
        List<String> aliases = List.of("delete", "kick", "promote", "transfer");

        NetworkPlayer np;

        List<String> results = new ArrayList<>();

        if (!(commandSender instanceof Player)) {
            return List.of();
        }

        Player p = (Player) commandSender;
        np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());
        com.carrotguy69.cxyz.classes.models.db.Party party = com.carrotguy69.cxyz.classes.models.db.Party.getPlayerParty(np.getUUID());



        // /party <something> <Player/GameServer>


        if (args.length <= 1) {
            for (String subcommand : subcommands) {
                if (subcommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    results.add(subcommand);
                }
            }
            for (String subcommand : aliases) {
                if (subcommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    results.add(subcommand);
                }
            }

            // If the string does not begin to match any subcommand, we will return the list of online players. Assuming they want to invite a player.

            if (results.isEmpty()) {
                for (NetworkPlayer pl : users.values()) {

                    if (pl.getDisplayName().toLowerCase().startsWith(args[0].toLowerCase())) {
                        if (pl.isOnline() && np.isVisibleTo(pl)) {
                            results.add(pl.getDisplayName());
                        }
                    }
                }
            }

            results.sort(String.CASE_INSENSITIVE_ORDER);
            return results;
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "chat":
                case "create":
                case "leave":
                case "disband":
                case "delete":
                case "list":
                case "private":
                case "public":
                    return List.of();



                case "invite":
                    for (NetworkPlayer pl : users.values()) {

                        if (pl.getDisplayName().toLowerCase().startsWith(args[1].toLowerCase())) {
                            if (pl.isOnline() && pl.isVisibleTo(np)) {
                                results.add(pl.getDisplayName());
                            }
                        }

                    }

                    results.sort(String.CASE_INSENSITIVE_ORDER);
                    return results;

                case "join":
                    for (UUID uuid : parties.keySet()) {
                        NetworkPlayer pl = NetworkPlayer.getPlayerByUUID(uuid); // A player who owns a party.

                        if (pl.getDisplayName().toLowerCase().startsWith(args[1].toLowerCase())) {
                            if (pl.isOnline() && pl.isVisibleTo(np)) {
                                results.add(pl.getDisplayName());
                            }
                        }
                    }

                    results.sort(String.CASE_INSENSITIVE_ORDER);
                    return results;

                case "remove":
                case "kick":
                case "setleader":
                case "transfer":
                case "promote":
                    if (party == null || party.getPlayers() == null) {
                        return List.of();
                    }

                    for (String playerUUID : party.getPlayers()) {
                        NetworkPlayer partyPlayer = NetworkPlayer.getPlayerByUUID(UUID.fromString(playerUUID));

                        if (partyPlayer.getDisplayName().toLowerCase().startsWith(args[1].toLowerCase())) {
                            if (partyPlayer.isOnline() && partyPlayer.isVisibleTo(np)) {
                                results.add(partyPlayer.getDisplayName());
                            }
                        }
                    }

                    results.sort(String.CASE_INSENSITIVE_ORDER);
                    return results;


                case "warp":
                    for (GameServer server : servers) {
                        if (server.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                            results.add(server.getName());
                        }

                    }

                    results.sort(String.CASE_INSENSITIVE_ORDER);
                    return results;
            }
        }

        if (args.length > 2) {
            return List.of();
        }

        return subcommands;
    }
}
