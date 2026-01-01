package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.classes.models.config.PlayerRank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.carrotguy69.cxyz.CXYZ.ranks;
import static com.carrotguy69.cxyz.CXYZ.users;

public class Rank implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            // suggest set or view
            return List.of("set", "view", "list");
        }

        if (args.length == 1) {
            List<String> all = List.of("set", "view", "list");
            List<String> results = new ArrayList<>();

            for (String s : all) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    results.add(s);
                }
            }

            return results;
        }

        if (args.length == 2) {

            switch (args[0]) {
                case "set":
                    List<String> rankList = new ArrayList<>();

                    for (PlayerRank rank : ranks) {
                        if (rank.getName().toLowerCase().startsWith(args[1].toLowerCase()))
                            rankList.add(rank.getName().toLowerCase());
                    }

                    return rankList;

                    case "view":

                    List<String> results = new ArrayList<>();

                    for (NetworkPlayer np : users.values()) {
                        // No need to check for online or vanish status, because this command accepts all players anyway, regardless of those statuses.
                        if (np.getDisplayName().toLowerCase().startsWith(args[1].toLowerCase())) {
                            results.add(np.getDisplayName());
                        }
                    }

                    return results;

                case "list":
                    return List.of();
            }


        }

        if (args.length == 3) {


            if (args[0].equals("set")) {
                List<String> results = new ArrayList<>();

                for (NetworkPlayer np : users.values()) {
                    // No need to check for online or vanish status, because this command accepts all players anyway, regardless of those statuses.
                    if (np.getDisplayName().toLowerCase().startsWith(args[2].toLowerCase())) {
                        results.add(np.getDisplayName());
                    }
                }

                return results;
            }

            else {
                return List.of();
            }
        }

        return List.of();
    }


}
