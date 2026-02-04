package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Print implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (CommandRestrictor.handleRestrictedTabCompleter(command, sender)) //
            return List.of();

        String node = "cxyz.admin.print";

        if (!sender.hasPermission(node)) {
            return List.of();
        }

        List<String> options = Arrays.asList("users", "parties", "partyinvites", "partyexpires", "punishments", "messages", "cosmetics", "channels", "announcements", "config", "msgconfig", "msgyml", "cosmeticyml", "cosmeticsyml", "chatfilter", "colors");

        options.sort(String.CASE_INSENSITIVE_ORDER);

        if (args.length == 0) {
            return options;
        }

        if (args.length == 1) {
            List<String> results = new ArrayList<>();

            for (String s : options) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    results.add(s);
                }
            }

            return results;
        }

        return List.of();
    }
}
