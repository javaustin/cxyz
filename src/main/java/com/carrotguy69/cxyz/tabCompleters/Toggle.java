package com.carrotguy69.cxyz.tabCompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Toggle implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        List<String> options = List.of("enable", "disable");

        for (String option : options) {
            if (option.startsWith(args[args.length - 1])) {
                results.add(option);
            }
        }

        return results;
    }
}
