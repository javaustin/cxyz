package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.utils.CommandRestrictor;
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

        String node = "cxyz.print";

        if (!sender.hasPermission(node)) {
            return List.of();
        }

        final List<String> EMPTY = new ArrayList<>();

        List<String> results = EMPTY;
        List<String> options = Arrays.asList("users", "parties", "partyinvites", "partyexpires", "punishments", "messages", "cosmetics", "channels", "announcements", "config", "msgconfig", "msgyml", "cosmeticyml", "cosmeticsyml", "chatfilter", "colors", "servers", "gameservers", "stats", "gamestats");;

        if (args.length == 0) {
            return options;
        }

        if (args.length == 1) {
            options = Arrays.asList("users", "parties", "partyinvites", "partyexpires", "punishments", "messages", "cosmetics", "channels", "announcements", "config", "msgconfig", "msgyml", "cosmeticyml", "cosmeticsyml", "chatfilter", "colors", "servers", "gameservers", "stats", "gamestats");
        }

        if (args.length == 2)
            if (args[0].toLowerCase().startsWith("user"))
                options = AnyPlayer.getAllUsernames();


        options.sort(String.CASE_INSENSITIVE_ORDER);
        for (String s : options) {
            if (s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                results.add(s);
            }
        }

        return results;
    }
}
