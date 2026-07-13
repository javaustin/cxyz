package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.utils.ObjectUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PowerTool implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("cxyz.powertool")) {
            return List.of();
        }

        List<String> results = new ArrayList<>();
        List<String> options = LocalOnlinePlayer.getUsernames();

        if (args.length == 0) {
            return LocalOnlinePlayer.getUsernames();
        }

        // same for args.length == 1

        if (args.length == 1) {
            try {
                options = new ArrayList<>(CXYZ.getCommands().keySet());
                Logger.debug("options: " + options);
            }
            catch (ReflectiveOperationException e) {
                Logger.debug("In powertool tab completer: " + e.getMessage());
                options.clear();
            }
        }

        NetworkPlayer target = NetworkPlayer.getPlayerByUsername(args[0]);

        if (args.length > 1 && target != null && target.getPlayer() != null) {
            Logger.debug("args: " + ObjectUtils.sliceToString(args, 0));
            String sudoCommandLine = ObjectUtils.sliceToString(args, 0);

            if (sudoCommandLine.startsWith("c:")) {
                return List.of();
            }

            List<String> suggestions = CXYZ.commandMap.tabComplete(target.getPlayer(), ObjectUtils.sliceToString(args, 0));
            Logger.debug("suggestions: " + suggestions);

            options = suggestions != null ? suggestions : List.of();
        }
        else if (args.length > 2) {
            options = List.of();
        }

        for (String option : options) {
            if (option.startsWith(args[args.length - 1])) {
                results.add(option);
            }
        }


        if (results.isEmpty()) {
            results.add("-r");
            results.add("-l");
            results.add("-b");
        }

        return results;
    }
}
