package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Unequip implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            return List.of();
        }

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId());


        List<String> cosmeticIDs = new ArrayList<>();

        if (np.getEquippedCosmetics() != null && !np.getEquippedCosmetics().isEmpty()) {
            for (Cosmetic cosmetic : np.getEquippedCosmetics()) {
                if (cosmetic.isEnabled())
                    cosmeticIDs.add(cosmetic.getId());
            }
        }

        List<String> options = new ArrayList<>();

        for (String s : cosmeticIDs) {
            if (s.toUpperCase().startsWith(args[0].toUpperCase())) {
                options.add(s);
            }
        }

        options.sort(String.CASE_INSENSITIVE_ORDER);
        return options;

    }
}
