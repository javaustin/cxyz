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

import static com.carrotguy69.cxyz.CXYZ.users;

public class Unignore implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            return List.of();
        }

        NetworkPlayer senderNP = NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId());

        List<String> usernames = new ArrayList<>();

        for (NetworkPlayer user : users.values()) {
            if (senderNP.isIgnoring(user))
                usernames.add(user.getDisplayName());
        }

        if (args.length == 0) {
            return usernames;
        }

        if (args.length == 1) {
            List<String> results = new ArrayList<>();

            for (String username : usernames) {
                if (username.toLowerCase().startsWith(args[0].toLowerCase())) {
                    results.add(username);
                }
            }

            return results;
        }

        return List.of();
    }
}
