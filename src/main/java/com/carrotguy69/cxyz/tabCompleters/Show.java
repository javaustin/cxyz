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

public class OnlineSelfPlayer implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    //      Returns options for the first argument in a given command.
    //      The options must be visible to that given player. So they must be online and unvanished OR vanished and under the senders rank hierarchy.
        Player p;
        NetworkPlayer np = null;

        if (sender instanceof Player) {
            p = (Player) sender;
            np = NetworkPlayer.resolvePlayer(p.getUniqueId());
        }

        List<String> visibleUsernames = OnlinePlayer.getVisibleUsernames(sender, np);


        if (np != null) {
            // Include ourselves
            visibleUsernames.add(np.getUsername());
            visibleUsernames.add(np.getDisplayName());
        }

        if (args.length == 0) {
            visibleUsernames.add("~");
            return visibleUsernames;
        }

        if (args.length == 1) {
            List<String> results = new ArrayList<>();
            visibleUsernames.add("~");

            for (String username : visibleUsernames) {
                if (username.toLowerCase().startsWith(args[0].toLowerCase())) {
                    results.add(username);
                }
            }

            return results;
        }

        else {
            return List.of();
        }

    }


}
