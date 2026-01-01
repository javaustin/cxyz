package com.carrotguy69.cxyz.cmd.admin;

import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Map;
import java.util.Properties;

import static com.carrotguy69.cxyz.CXYZ.f;

public class Motd implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.admin.motd";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }


        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MOTD_VIEW, Map.of("motd", Bukkit.getMotd()));
            return true;
        }

        String motd = String.join(" ", args)
                .replace("\\n", "\n");

        Bukkit.getServer().setMotd(f(motd));

        try {
            Properties properties = new Properties();
            File file = new File("server.properties"); // modifies the actual server.properties value.
            FileInputStream in = new FileInputStream(file);
            properties.load(in);
            in.close();

            properties.setProperty("motd", f(motd));

            FileOutputStream out = new FileOutputStream(file);
            properties.store(out, null);
            out.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        MessageUtils.sendParsedMessage(sender, MessageKey.MOTD_SET, Map.of("motd", motd));

        return true;
    }
}
