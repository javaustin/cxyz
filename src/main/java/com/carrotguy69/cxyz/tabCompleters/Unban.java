package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.models.db.Punishment;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.carrotguy69.cxyz.CXYZ.punishmentIDMap;

public class Unban implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (CommandRestrictor.handleRestrictedTabCompleter(command, sender))
            return List.of();

        String node = "cxyz.mod.ban";

        if (!sender.hasPermission(node)) {
            return List.of();
        }

        List<String> usernames = getBannedUsernames();

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

        else {
            return List.of();
        }



    }

    private List<String> getBannedUsernames() {
        List<String> bannedUsernames = new ArrayList<>();

        Map<Long, Punishment> map = Map.copyOf(punishmentIDMap);

        for (Map.Entry<Long, Punishment> entry : map.entrySet()) {
            Punishment punishment = entry.getValue();
            if (punishment.isEnforced() && Objects.equals(punishment.getType(), Punishment.PunishmentType.BAN)) {
                bannedUsernames.add(punishment.getUsername());
            }
        }

        return bannedUsernames;
    }
}
