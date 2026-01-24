package com.carrotguy69.cxyz.models.config.shorthand;

import com.carrotguy69.cxyz.models.config.shorthand.interfaces.ShorthandExecutor;
import com.carrotguy69.cxyz.models.config.shorthand.interfaces.ShorthandTabCompleter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ShorthandBukkitCommand extends Command {

    private final ShorthandExecutor executor;
    private final ShorthandTabCompleter tabCompleter;

    public ShorthandBukkitCommand(
            String name,
            ShorthandExecutor executor,
            ShorthandTabCompleter tabCompleter
    ) {
        super(name);
        this.executor = executor;
        this.tabCompleter = tabCompleter;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return executor.execute(sender, label, args);
    }

    @Override
    public @NotNull List<String> tabComplete(
            @NotNull CommandSender sender,
            @NotNull String alias,
            String[] args
    ) {
        return tabCompleter.tabComplete(sender, alias, args);
    }
}

