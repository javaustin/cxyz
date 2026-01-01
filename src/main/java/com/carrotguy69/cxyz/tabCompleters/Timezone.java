package com.carrotguy69.cxyz.tabCompleters;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZoneId;
import java.util.*;

public class Timezone implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        List<String> availableTimezones = new ArrayList<>(ZoneId.getAvailableZoneIds());

        if (args.length == 0) {
            return availableTimezones;
        }

        if (args.length == 1) {

            List<String> results = new ArrayList<>();
            for (String timezone : availableTimezones) {
                if (timezone.toLowerCase().startsWith(args[0].toLowerCase())) {
                    results.add(timezone);
                }
            }

            return results;
        }

        else {
            return List.of();
        }
    }

}
