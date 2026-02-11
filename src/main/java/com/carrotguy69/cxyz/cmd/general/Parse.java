package com.carrotguy69.cxyz.cmd.general;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageParser;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.SimpleTextComponent;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parse implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /parse <message>
            /parse (&eHello World)[HOVER:&aClick me!][RUN_COMMAND:/parse Thanks {player}!]
        */

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.general.parse";


        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        String content = String.join(" ", args);

        Map<String, Object> commonMap = new HashMap<>();

        if (sender instanceof Player) {
            Player p = (Player) sender;

            commonMap.putAll(MapFormatters.playerFormatter(NetworkPlayer.getPlayerByUUID(p.getUniqueId())));
        }

        try {
            MessageParser parser = new MessageParser(content, commonMap);
            List<SimpleTextComponent> components = parser.parse();

            sender.spigot().sendMessage(parser.toTextComponent(components));

        }

        catch (MessageParser.MessageParseException ex) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PARSE_ERROR, Map.of("error", ex.getMessage(), "text", content));
        }

        return true;
    }
}
