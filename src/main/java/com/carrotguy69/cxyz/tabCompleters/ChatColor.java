package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.CXYZ;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChatColor implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String name, @NotNull String[] args) {

        List<String> colors = new ArrayList<>(CXYZ.colorMap.keySet());
        colors.add("reset");

        if (args.length == 0) {
            return colors;
        }

        List<String> results = new ArrayList<>();

        for (String s : colors) {
            if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                results.add(s);
            }
        }

        if (results.isEmpty()) {
            colors.addAll(CXYZ.colorMap.values());

            for (String s : colors) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    results.add(s);
                }
            }
        }

        return results;
    }


}
