package com.carrotguy69.cxyz.cmd.mod.punishment;

import com.carrotguy69.cxyz.classes.models.db.Punishment;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.template.MapFormatters;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.punishmentIDMap;

public class Delete implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.mod.punishment.delete";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

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
