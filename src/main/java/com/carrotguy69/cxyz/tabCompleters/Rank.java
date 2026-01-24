package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.config.PlayerRank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.carrotguy69.cxyz.CXYZ.ranks;
import static com.carrotguy69.cxyz.CXYZ.users;

public class Rank implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        List<String> all = List.of("add", "remove", "list");

        if (args.length == 0) {
            return all;
        }

        if (args.length == 1) {
            List<String> results = new ArrayList<>();

            for (String s : all) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    results.add(s);
                }
            }

            return results;
        }

        if (args.length == 2) {

            List<String> results = new ArrayList<>();

            switch (args[0]) {
                case "add":

                    for (PlayerRank rank : ranks) {
                        if (rank.getName().toLowerCase().startsWith(args[1].toLowerCase()))
                            results.add(rank.getName().toLowerCase());
                    }

                    return results;

                case "remove":

                    if (!(sender instanceof Player)) {
                        for (PlayerRank rank : ranks) {
                            if (rank.getName().toLowerCase().startsWith(args[1].toLowerCase()))
                                results.add(rank.getName().toLowerCase());
                        }
                    }

                    else {
                        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId());

                        for (PlayerRank rank : np.getRanks()) {
                            if (rank.getName().toLowerCase().startsWith(args[1].toLowerCase()))
                                results.add(rank.getName().toLowerCase());
                        }
                    }

                    return results;

                case "list":

                    for (NetworkPlayer np : users.values()) {
                        // No need to check for online or vanish status, because this command accepts all players anyway, regardless of those statuses.
                        if (np.getDisplayName().toLowerCase().startsWith(args[1].toLowerCase())) {
                            results.add(np.getDisplayName());
                        }
                    }

                    return results;
            }
        }

        return List.of();
    }


}
