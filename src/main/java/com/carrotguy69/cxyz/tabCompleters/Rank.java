package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.config.PlayerRank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.carrotguy69.cxyz.CXYZ.ranks;
import static com.carrotguy69.cxyz.CXYZ.users;

public class Rank implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        List<String> results = new ArrayList<>();
        List<String> options = new ArrayList<>(List.of("add", "remove", "list"));
        options.removeIf(subcommand -> !(sender.hasPermission(String.format("cxyz.rank.%s", subcommand))));

        if (args.length == 2) {
            if (!sender.hasPermission(String.format("cxyz.rank.%s", args[0])))
                return List.of();

            switch (args[0].toLowerCase()) {
                case "add":
                    options = ranks.stream().map(PlayerRank::getName).collect(Collectors.toList());
                    break;


                case "remove":
                    if (sender instanceof Player) {
                        NetworkPlayer np = NetworkPlayer.resolvePlayer(((Player) sender).getUniqueId());
                        options = np.getRanks().stream().map(PlayerRank::getName).collect(Collectors.toList());
                    }
                    break;

                case "list":
                    options = users.values().stream().map(NetworkPlayer::getDisplayName).collect(Collectors.toList());
                    break;
            }
        }

        if (args.length == 3) {
            options = AnyPlayer.getAllUsernames();
        }

        if (args.length >= 4) {
            options = List.of();
        }

        if (options.isEmpty() && args.length == 2) {
            // The default behavior in /rank remove is to only list ranks that a sender has.
            // If a sender does not have any relevant ranks to remove or is typing something different, we will return all the ranks, not just the senders.
                options = ranks.stream().map(PlayerRank::getName).collect(Collectors.toList());
        }

        for (String option : options) {
            if (option.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                results.add(option);
            }
        }

        return results;
    }


}
