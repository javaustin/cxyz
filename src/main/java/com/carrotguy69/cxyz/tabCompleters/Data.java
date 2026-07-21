package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Data implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> options = new ArrayList<>();
        List<String> results = new ArrayList<>();

        if (args.length == 0) {
            return options;
        }

        if (args.length == 1) {
            options = List.of("networkplayer", "message");
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "networkplayer":
                    options = List.of("set", "get", "set-async", "get-async");
                    break;
                case "message":
                    options = List.of("get", "get-async");
                    break;
            }
        }

        if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "networkplayer":
                    options = AnyPlayer.getAllUsernames();
                    options.addAll(CXYZ.uuids);
                    break;
                case "message":
                    options = List.of("as_sender", "as_recipient");
                    break;
            }
        }

        if (args.length == 4) {
            switch (args[0].toLowerCase()) {
                case "networkplayer":
                    options = Arrays.stream(NetworkPlayer.class.getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
                    break;

                case "message":
                    options = AnyPlayer.getAllUsernames();
                    options.addAll(CXYZ.uuids);
                    break;
            }
        }


        for (String option : options) {
            if (option == null)
                continue;

            if (option.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                results.add(option);
            }
        }

        results.sort(String.CASE_INSENSITIVE_ORDER);

        return results;
    }
}
