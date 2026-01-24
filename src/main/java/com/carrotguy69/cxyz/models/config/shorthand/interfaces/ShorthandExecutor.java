package com.carrotguy69.cxyz.models.config.shorthand.interfaces;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface ShorthandExecutor {
    boolean execute(CommandSender sender, String label, String[] args);
}
