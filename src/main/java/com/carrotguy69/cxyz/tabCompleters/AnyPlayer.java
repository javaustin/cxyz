package com.carrotguy69.cxyz.tabCompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.carrotguy69.cxyz.CXYZ.usernames;

public class AnyPlayer implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        //      Returns options for the first argument in a given command.
        //      Any player that is a NetworkPlayer and not banned (regardless of online status or not can be returned.)

        List<String> visibleUsernames = getAllUsernames();

        if (args.length == 0) {
            return visibleUsernames;
        }

        if (args.length == 1) {
            List<String> results = new ArrayList<>();

            for (String username : visibleUsernames) {
                if (username.toLowerCase().startsWith(args[0].toLowerCase())) {
                    results.add(username);
                }
            }

            return results;
        }

        else {
            return List.of();
        }

    }

    public static List<String> getAllUsernames() {
        return usernames;
    }
}
