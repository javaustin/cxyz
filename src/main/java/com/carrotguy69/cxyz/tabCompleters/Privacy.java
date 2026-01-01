package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Privacy implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> values = new ArrayList<>();
        List<String> results = new ArrayList<>();

        switch (command.getName()) {
            case "friendprivacy":
                values = new ArrayList<>();
                for (NetworkPlayer.FriendRequestPrivacy v : NetworkPlayer.FriendRequestPrivacy.values()) {
                    values.add(v.name());
                }

                if (args.length == 0) {
                    return values;
                }


                results = new ArrayList<>();
                // if args.length >= 1

                for (String s : values) {
                    if (s.startsWith(args[1])) {
                        results.add(s);
                    }
                }

                return results;

            case "messageprivacy":
                for (NetworkPlayer.MessagePrivacy v : NetworkPlayer.MessagePrivacy.values()) {
                    values.add(v.name());
                }

                if (args.length == 0) {
                    return values;
                }


                // if args.length >= 1

                for (String s : values) {
                    if (s.startsWith(args[1])) {
                        results.add(s);
                    }
                }

                return results;

            case "partyprivacy":
                for (NetworkPlayer.PartyInvitePrivacy v : NetworkPlayer.PartyInvitePrivacy.values()) {
                    values.add(v.name());
                }

                if (args.length == 0) {
                    return values;
                }


                // if args.length >= 1

                for (String s : values) {
                    if (s.startsWith(args[1])) {
                        results.add(s);
                    }
                }

                return results;
        }

        return List.of();
    }
}
