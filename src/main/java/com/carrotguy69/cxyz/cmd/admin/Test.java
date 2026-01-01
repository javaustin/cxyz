package com.carrotguy69.cxyz.cmd.admin;

import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.f;

public class Test implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {


        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.admin.test";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        String unparsed = String.join(" ", args);
        String fString = f(unparsed);
        String forced = MessageUtils.forceColor(unparsed);

        Logger.log("unparsed: " + unparsed);
        Logger.log("fString" + fString);
        Logger.log("forced" + forced);

        sender.sendMessage("expected: " + fString);
        sender.spigot().sendMessage(new TextComponent("actual: " + f(forced)));

        TextComponent tc = new TextComponent("actual but red: " + f(forced));
        tc.setColor(ChatColor.of("#F82055"));

        sender.spigot().sendMessage(tc);


        return true;
    }
}
