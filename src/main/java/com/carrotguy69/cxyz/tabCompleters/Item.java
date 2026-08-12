package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Item implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        NetworkPlayer np;

        if (sender instanceof Player p) {
            np = NetworkPlayer.resolvePlayer(p.getUniqueId());
        }
        else {
            np = null;
        }

        List<String> results = new ArrayList<>();
        List<String> options = Registry.MATERIAL.stream().map(Material::name).map(String::toLowerCase).collect(Collectors.toList());

        if (args.length == 0) {
            return options;
        }


        // bypass args.length == 1 (returns same thing as above)

        if (args.length == 2) {
            options = List.of("1", "16", "32", "64");
        }


        if (args.length == 3) {
            options = LocalOnlinePlayer.getUsernames(np);
        }

        if (args.length >= 4) {
            options = List.of();
        }


        for (String s : options) {
            if (s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                results.add(s);
            }
        }

        return results;
    }
}
