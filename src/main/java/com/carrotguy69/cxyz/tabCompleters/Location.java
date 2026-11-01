package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.utils.ObjectUtils;
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

        String node = "location";
        String node2 = "location.others";

        if (!sender.hasPermission("cxyz." + node)) {
            return List.of();
        }

        if (sender instanceof Player) {
            p = (Player) sender;
            np = NetworkPlayer.resolvePlayer(p.getUniqueId());
        }

        boolean longFlag = String.join(" ", args).contains("-l");
        args = ObjectUtils.removeItem(args, "-l");

        List<String> results = new ArrayList<>();
        List<String> options = new ArrayList<>();


        if (args.length == 0 && sender.hasPermission("cxyz." + node2)) {
            return options;
        }

        if (args.length == 1 && sender.hasPermission("cxyz." + node2)) {
            options = OnlinePlayer.getVisibleUsernames(sender, np);
        }

        if (args.length == 2 && args[0].contains("-l") && sender.hasPermission("cxyz." + node2)) {
            options = OnlinePlayer.getVisibleUsernames(sender, np);
        }

        for (String option : options) {
            if (option.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                results.add(option);
            }
        }

        if (results.isEmpty() && !longFlag && args.length <= 2) {
            results.add("-l");
        }

        return results;
    }
}
