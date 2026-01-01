package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.carrotguy69.cxyz.CXYZ.users;

public class XP implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String name, @NotNull String[] args) {

        if (args.length == 0) {
            return List.of("add", "remove", "set", "view");
        }

        if (args.length == 1) {
            List<String> all = List.of("add", "remove", "set", "view");
            List<String> results = new ArrayList<>();

            for (String s : all) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    results.add(s);
                }
            }

            return results;
        }

        // args[0] counts as an arg (add/remove/set/view)

        if (args.length == 2) {
            if (args[0].equals("view")) {

                List<String> results = new ArrayList<>();

                for (NetworkPlayer np : users.values()) {
                    // No need to check for online or vanish status, because this command accepts all players anyway, regardless of those statuses.
                    if (np.getDisplayName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        results.add(np.getDisplayName());
                    }
                }

                return results;

            }

            else {
                // If the command is not view: we can suggest numbers for the player to add

                return List.of("1", "5", "10", "20", "50", "100");

            }
        }

        if (args.length == 3) {
            if (args[0].equals("add") || args[0].equals("remove") || args[0].equals("set")) {
                List<String> results = new ArrayList<>();

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
