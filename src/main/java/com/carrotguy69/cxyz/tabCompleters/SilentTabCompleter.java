package com.carrotguy69.cxyz.tabCompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Adds the common -s flag after the wrapped completer has exhausted its options. */
public class SilentTabCompleter implements TabCompleter {
    private final TabCompleter delegate;

    public SilentTabCompleter(TabCompleter delegate) {
        this.delegate = delegate;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = delegate.onTabComplete(sender, command, alias, args);
        if (args.length > 0 && args[args.length - 1].equals("-")
                && (suggestions == null || suggestions.stream().noneMatch(option -> option.startsWith("-")))) {
            return List.of("-s");
        }

        return suggestions;
    }
}
