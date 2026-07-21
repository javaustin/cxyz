package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Enchant implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {


        NetworkPlayer np = null;
        if (sender instanceof Player) {
            np = NetworkPlayer.resolvePlayer(((Player) sender).getUniqueId());
        }

        List<String> results = new ArrayList<>();
        List<String> options = new ArrayList<>();

        // enchant <enchantment> [level] [player]

        List<String> enchants = Registry.ENCHANTMENT.stream().map(Enchantment::getKey).map(NamespacedKey::getKey).collect(Collectors.toList());
        enchants.add("clear");

        if (args.length == 0) {
            return enchants;
        }

        if (args.length == 1) {
            options = enchants;
        }

        boolean clear = args[0].equalsIgnoreCase("clear");

        if (args.length == 2 && clear) {
            options = LocalOnlinePlayer.getUsernames(np);
        }

        else if (args.length == 2) {
            Enchantment enchantment = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(args[0]));

            if (enchantment != null) {
                int max = enchantment.getMaxLevel();

                for (int i = 1; i <= max ; i++) {
                    options.add(String.valueOf(i));
                }
            }
        }

        if (args.length == 3 && !clear) {
            options = LocalOnlinePlayer.getUsernames(np);
        }

        if (args.length == 3 && clear) {
            options = List.of();
        }


        for (String option : options) {
            if (option.startsWith(args[args.length - 1])) {
                results.add(option);
            }
        }

        return results;
    }
}
