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

public class Location implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p;
        NetworkPlayer np = null;

        if (sender instanceof Player) {
            p = (Player) sender;
            np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());
        }

        List<String> results = new ArrayList<>();
        List<String> usernames = OnlinePlayer.getVisibleUsernames(sender, np);

        if (args.length == 0) {
            return results;
        }

        for (String name : usernames) {
            if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
                results.add(name);
            }
        }

        if (results.isEmpty()) {
            results.add("-l");
        }

        return results;
    }
}
