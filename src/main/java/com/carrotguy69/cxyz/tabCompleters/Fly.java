package com.carrotguy69.cxyz.tabCompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Fly implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> options = List.of("enable", "disable");
        List<String> results = new ArrayList<>();


        if (args.length == 0) {
            return options;
        }

        if (args.length == 2) {
            options = LocalOnlinePlayer.getUsernames();
        }

        if (args.length >= 2) {
            return List.of();
        }

        for (String s : options) {
            if (s.startsWith(args[args.length - 1])) {
                results.add(s);
            }
        }

        return results;
    }
}
