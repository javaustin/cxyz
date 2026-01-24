package com.carrotguy69.cxyz.models.config.shorthand.interfaces;

import org.bukkit.command.CommandSender;

import java.util.List;

@FunctionalInterface
public interface ShorthandTabCompleter {
    List<String> tabComplete(CommandSender sender, String alias, String[] args);
}
