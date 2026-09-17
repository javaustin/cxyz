package com.carrotguy69.cxyz.other;

import com.carrotguy69.cxyz.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/** Clears command-local flags after execution so they cannot affect the next command. */
public class SilentCommandExecutor implements CommandExecutor {
    private final CommandExecutor delegate;

    public SilentCommandExecutor(CommandExecutor delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        try {
            return delegate.onCommand(sender, command, label, args);
        }
        finally {
            CommandUtils.clearSilent();
        }
    }
}
