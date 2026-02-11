package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Punishment implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (CommandRestrictor.handleRestrictedTabCompleter(command, sender))
            return List.of();

        String node = "cxyz.mod.punishment";

        if (!sender.hasPermission(node)) {
            return List.of();
        }

        List<String> subcommands = List.of("delete", "edit", "history", "info", "remove");

        if (args.length == 0) {
            return subcommands;
        }

        List<String> results = new ArrayList<>();

        if (args.length == 1) {
            for (String s : subcommands) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    results.add(s);
                }
            }

            return results;
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {

                case "delete":
                case "remove":
                case "info":

                case "edit":
                    return List.of();

                case "clear":
                case "history":
                    return AnyPlayer.getAllUsernames();

            }
        }

        if (args.length == 3) {
            // attributes for edit
            if (args[0].equalsIgnoreCase("edit")) {
                List<String> options = List.of("enforced", "reason", "duration");

                for (String s : options) {
                    if (s.toLowerCase().startsWith(args[2].toLowerCase())) {
                        results.add(s);
                    }
                }

                return results;
            }
            return List.of();
        }

        if (args.length == 4) {
            // value

            if (
                    args[0].equalsIgnoreCase("edit")
                    && args[2].equalsIgnoreCase("enforced")
            ) {
                return List.of("true", "false");
            }

            if (
                    args[0].equalsIgnoreCase("edit")
                            && args[2].equalsIgnoreCase("duration")
            ) {
                results = new ArrayList<>(List.of(
                        "1m", "5m", "30m", "1h", "3h", "12h", "1d", "1w", "1m", "1y", "permanent"
                ));

                results.sort(String.CASE_INSENSITIVE_ORDER);
                return results;
            }
        }

        return List.of();
    }
}
