package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.carrotguy69.cxyz.CXYZ.cosmetics;

public class Cosmetic implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {


        List<String> subcommands = List.of("buy", "equip", "list", "unequip");

        if (args.length == 0) {
            return subcommands;
        }


        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            for (String s : subcommands) {
                if (s.toUpperCase().startsWith(args[0].toUpperCase())) {
                    results.add(s);
                }
            }

            return results;
        }


        List<String> cosmeticIDs;
        List<String> options;
        NetworkPlayer np;

        switch (args[0]) {

            case "buy":

                cosmeticIDs = new ArrayList<>();

                if (cosmetics != null && !cosmetics.isEmpty()) {
                    for (com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic cosmetic : cosmetics) {
                        if (cosmetic.isEnabled())
                            cosmeticIDs.add(cosmetic.getId());
                    }
                }

                options = new ArrayList<>();

                for (String s : cosmeticIDs) {
                    if (s.toUpperCase().startsWith(args[1].toUpperCase())) {
                        options.add(s);
                    }
                }

                options.sort(String.CASE_INSENSITIVE_ORDER);
                return options;

            case "equip":
                if (!(sender instanceof Player)) {
                    return List.of();
                }

                np = NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId());
                cosmeticIDs = new ArrayList<>();

                if (np.getOwnedCosmetics() != null && !np.getOwnedCosmetics().isEmpty()) {
                    for (com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic cosmetic : np.getOwnedCosmetics()) {
                        if (cosmetic.isEnabled()) {
                            cosmeticIDs.add(cosmetic.getId());
                        }
                    }
                }

                options = new ArrayList<>();


                for (String s : cosmeticIDs) {
                    if (s.toUpperCase().startsWith(args[1].toUpperCase())) {
                        options.add(s);
                    }
                }

                options.sort(String.CASE_INSENSITIVE_ORDER);
                return options;

            case "unequip":
                if (!(sender instanceof Player)) {
                    return List.of();
                }

                np = NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId());


                cosmeticIDs = new ArrayList<>();

                if (np.getEquippedCosmetics() != null && !np.getEquippedCosmetics().isEmpty()) {
                    for (com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic cosmetic : np.getEquippedCosmetics()) {
                        if (cosmetic.isEnabled())
                            cosmeticIDs.add(cosmetic.getId());
                    }
                }

                options = new ArrayList<>();

                for (String s : cosmeticIDs) {
                    if (s.toUpperCase().startsWith(args[1].toUpperCase())) {
                        options.add(s);
                    }
                }

                options.sort(String.CASE_INSENSITIVE_ORDER);
                return options;

            case "list":
                if (args.length == 2) {
                    List<String> listSubcommands = new ArrayList<>(List.of("all", "equipped", "owned"));
                    options = new ArrayList<>();

                    for (String s : listSubcommands) {
                        if (s.toUpperCase().startsWith(args[1].toUpperCase())) {
                            options.add(s);
                        }
                    }

                    return options;
                }

                if (args.length == 3) {
                    if (args[1].equalsIgnoreCase("all")) {
                        return List.of();
                    }

                    List<String> usernames = AnyPlayer.getAllUsernames();
                    options = new ArrayList<>();

                    for (String s : usernames) {
                        if (s.toUpperCase().startsWith(args[2].toUpperCase())) {
                            options.add(s);
                        }
                    }

                    return options;
                }
        }

        return List.of();
    }
}
