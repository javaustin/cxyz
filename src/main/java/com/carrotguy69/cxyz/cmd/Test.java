package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Test implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        String node = "cxyz.test";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        /*
        SYNTAX:
            /test ...
            /test whatever
        */
//
//        ConfigurationSection section = CXYZ.configYaml.getConfigurationSection("a-webhook");
//
//        if (section == null) {
//            Logger.warning("Section not found!");
//            return true;
//        }
//
//        DiscordWebhook webhook = WebhookMessageParser.createWebhook(section);
//
//        if (webhook == null) {
//            Logger.warning("Embed failed!");
//            return true;
//        }
//
//        webhook.send();

        return true;
    }
}
