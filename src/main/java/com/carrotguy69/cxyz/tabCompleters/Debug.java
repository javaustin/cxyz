package com.carrotguy69.cxyz.tabCompleters;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Debug implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        List<String> availableDebuggers = Arrays.stream(com.carrotguy69.cxyz.cmd.admin.Debug.DebugValue.values())
                .map(com.carrotguy69.cxyz.cmd.admin.Debug.DebugValue::name).collect(Collectors.toList());

        if (args.length == 0) {
            return availableDebuggers;
        }

        if (args.length == 1) {

            List<String> results = new ArrayList<>();
            for (String dbg : availableDebuggers) {
                if (dbg.toLowerCase().startsWith(args[0].toLowerCase())) {
                    results.add(dbg);
                }
            }

            return results;
        }

        else {
            return List.of();
        }
    }

}
