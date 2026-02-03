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

import static com.carrotguy69.cxyz.CXYZ.channels;
import static com.carrotguy69.cxyz.CXYZ.users;

public class ChatChannel implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player p;
        NetworkPlayer np;

        if (sender instanceof Player) {
            p = (Player) sender;
            np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());
        }

        else
            return List.of();

        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].toLowerCase();
        }

        List<String> allowedChannels = new ArrayList<>();
        for (BaseChannel channel : BaseChannel.getAllChannels()) {
            if (np.canAccessChannel(channel)) {
                allowedChannels.add(channel.getName().toLowerCase());
            }
        }

        List<String> allowedAliases = new ArrayList<>();
        for (BaseChannel channel : BaseChannel.getAllChannels()) {
            if (np.canAccessChannel(channel)) {
                allowedAliases.addAll(channel.getAliases());
            }
        }

        List<String> subcommands = new ArrayList<>(List.of("set", "ignore", "unignore"));

        if (args.length == 0) {
            subcommands.sort(String.CASE_INSENSITIVE_ORDER);
            return subcommands;
        }

        if (args.length == 1) {
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

            if (results.isEmpty()) { // Then start recommending subcommands if no channels match
                for (String s : subcommands) {
                    if (s.startsWith(args[0])) {
                        results.add(s);
                    }
                }
            }

            return results;
        }

        if (args.length == 2) {
            List<String> results;

            switch (args[0].toLowerCase()) {
                case "set":
                    results = new ArrayList<>();

                    for (String s : allowedChannels) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            results.add(s);
                        }
                    }

                    return results;

                case "ignore":
                    results = new ArrayList<>();

                    for (String s : np.getUnmutedChannels()) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase()) && allowedChannels.contains(s)) {
                            results.add(s);
                        }
                    }

                    return results;

                case "unignore":
                    results = new ArrayList<>();

                    for (String s : np.getMutedChannels()) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase()) && allowedChannels.contains(s)) {
                            results.add(s);
                        }
                    }

                    return results;



                default:
                    return List.of();
            }
        }

        return List.of();
    }

    public static List<String> getVisibleChannels(CommandSender sender, NetworkPlayer np) {
        List<String> visibleChannels = new ArrayList<>();


        for (BaseChannel channel : channels) {

            if (!(sender instanceof Player) || np == null) {
                visibleChannels.add(channel.getName());
            }

            else {
                if (np.canAccessChannel(channel)) {
                    visibleChannels.add(channel.getName());
                }
            }
        }

        visibleChannels.sort(String.CASE_INSENSITIVE_ORDER);

        return visibleChannels;

    }
}
