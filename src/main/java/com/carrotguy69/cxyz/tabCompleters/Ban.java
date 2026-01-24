package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.other.utils.ObjectUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class Ban implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        /*
        ex: "/ban player 1h20m hacking"
        args[0] is always the player
        args[1] is always the duration (can be "permanent")
        args[2] is always the reason (if present)
        ^ enforce these with tab completer

        - if not all args are present, we can make a cool inventory gui system. this can be rolled out in a future version (v1.2)

        flags:
        -s: silent, do not announce to chat
        */

        if (CommandRestrictor.handleRestrictedTabCompleter(command, sender))
            return List.of();

        String node = "cxyz.mod.ban";

        if (!sender.hasPermission(node)) {
            return List.of();
        }

        if (args.length == 0) {
            return AnyPlayer.getAllUsernames();
        }

        if (List.of(args).contains("-s")) {
            args = ObjectUtils.removeItem(args, "-s");
        }

        if (args[args.length - 1].equalsIgnoreCase("-")) {
            return List.of("-s");
        }

        NetworkPlayer np = null;
        if (sender instanceof Player) {
            np = NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId());
        }


        if (args.length == 1) {
            List<String> results = new ArrayList<>();

            for (String username : AnyPlayer.getAllUsernames()) {
                if (username.toLowerCase().startsWith(args[0].toLowerCase())) {
                    results.add(username);
                }
            }

//            if (np != null) {
//                results.add(np.getUsername());
//
//                if (np.getNickname() != null) {
//                    results.add(np.getNickname());
//                }
//            }

            results.sort(String.CASE_INSENSITIVE_ORDER);
            return results;
        }

        if (args.length == 2) {
            // Be mindful of the order we use if/elif statements in

            if (args[1].matches("\\d+")) { // If the arg only has numbers, suggest time units
                return durationGenerator(args[1]);
            }

            else if (args[1].matches("^([0-9]+[smhdwy])+$")) { // True if the arg has numbers and time units
                return new ArrayList<>(List.of(args[1]));
            }

            else if (args[1].isEmpty()) {
                List<String> results = new ArrayList<>(List.of( // ArrayList<>() is my attempt of making Bukkit return these in the order below.
                        "1m", "5m", "30m", "1h", "3h", "12h", "1d", "1w", "1m", "1y", "permanent"
                ));

                results.sort(String.CASE_INSENSITIVE_ORDER);
                return results;
            }

            else if ("permanent".startsWith(args[1].toLowerCase())) {
                return List.of("permanent");
            }

        }

        if (args.length == 3) {

            List<String> results = new ArrayList<>();
            List<String> all = List.of("...");

            for (String st : all) {
                if (st.toLowerCase().startsWith(args[2].toLowerCase())) {
                    results.add(st);
                }
            }

            return results;
        }

        return List.of();
    }

    private List<String> durationGenerator(String arg) {
        return new ArrayList<>(List.of(arg + "s", arg + "m", arg + "h", arg + "d", arg + "w", arg + "y"));
    }

}
