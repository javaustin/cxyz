package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Unlock implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].toLowerCase();
        }

        List<String> allowedChannels = new ArrayList<>();
        List<String> allowedAliases = new ArrayList<>();

        for (BaseChannel channel : BaseChannel.getAllChannels()) {
            if (!channel.isLocked())
                continue;

            if (!(sender instanceof Player)) {
                allowedChannels.add(channel.getName().toLowerCase());
                allowedAliases.addAll(channel.getAliases());
            }

            else {
                NetworkPlayer np = NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId());

                if (np.canAccessChannel(channel)) {
                    allowedChannels.add(channel.getName().toLowerCase());
                    allowedAliases.addAll(channel.getAliases());
                }
            }
        }

        if (args.length == 0) {
            return allowedChannels;
        }

        List<String> results = new ArrayList<>();

        for (String s : allowedChannels) { // recommend channels first
            if (s.startsWith(args[0])) {
                results.add(s);
            }
        }

        if (results.isEmpty()) { // if nothing matches, we will add these aliases and try again
            for (String s : allowedAliases) {
                if (s.startsWith(args[0])) {
                    results.add(s);
                }
            }
        }

        return results;
    }
}
