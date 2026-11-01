package com.carrotguy69.cxyz.cmd.punishment.manager;

import com.carrotguy69.cxyz.models.db.Punishment;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.models.config.CommandRestriction;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.punishmentIDMap;

public class PunishmentDelete implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        args = CommandUtils.handleSilent(args);

        /*
        SYNTAX:
            /punishment delete <id>
            /punishment delete 67
        */

        String node = "punishment.delete";
        if (!sender.hasPermission("cxyz." + node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestriction.handleRestricted(node, sender))
            return true;



        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "punishment"));
            return true;
        }

        long id;

        try {
           id = Long.parseLong(args[0]);
        }
        catch (NumberFormatException e) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_NUMBER, Map.of("input", args[0]));
            return true;
        }

        Punishment punishment = punishmentIDMap.get(id);
        if (punishment == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_PUNISHMENT, Map.of("input", args[0]));
            return true;
        }

        punishment.delete(); // To sync with API. This calls {api}/punishment/edit w/ post data
        punishmentIDMap.remove(id, punishment);

        Map<String, Object> commonMap = MapFormatters.senderFormatter(sender);

        commonMap.putAll(MapFormatters.punishmentFormatter(sender, punishment));

        MessageUtils.sendParsedMessage(sender, MessageKey.PUNISHMENT_DELETE, commonMap);

        return true;
    }
}
