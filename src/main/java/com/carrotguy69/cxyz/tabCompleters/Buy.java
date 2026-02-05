package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.carrotguy69.cxyz.CXYZ.cosmetics;

public class Buy implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        List<String> cosmeticIDs = new ArrayList<>();

        if (cosmetics != null && !cosmetics.isEmpty()) {
            for (Cosmetic cosmetic : cosmetics) {
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
