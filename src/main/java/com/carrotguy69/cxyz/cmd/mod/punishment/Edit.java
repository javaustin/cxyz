package com.carrotguy69.cxyz.cmd.mod.punishment;

import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.classes.models.db.Punishment;
import com.carrotguy69.cxyz.other.ObjectUtils;
import com.carrotguy69.cxyz.other.TimeUtils;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.template.MapFormatters;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.punishmentIDMap;
import static com.carrotguy69.cxyz.other.TimeUtils.validTimeString;

public class Edit implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.mod.punishment.edit";
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

        if (args.length == 1) {
            // only the id was provided, we will return punishment info.

            new Info().onCommand(sender, command, label, args);
            return true;
        }

        if (args.length == 2) {
            // not enough, we need an attribute and a value
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "value"));
            return true;
        }


        String stringValue = ObjectUtils.slice_(args, 2);
        Map<String, Object> commonMap = new HashMap<>();

        switch (args[1].toLowerCase()) {
            case "enforced":
                boolean value;

                if (stringValue.equalsIgnoreCase("true")) {
                    value = true;
                }

                else if (stringValue.equalsIgnoreCase("false")) {
                    value = false;
                }

                else {
                    MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_BOOLEAN, Map.of("input", args[0]));
                    return true;
                }

                punishment.setEnforced(value);

                commonMap.put("attribute", "enforced");
                commonMap.put("value", value);

                break;
                // Create PUNISHMENT_EDIT key. "Successfully updated set attribute {attribute} to {value}" w/ senderFormatter and punishmentFormatter

            case "reason":
                // allow all

                punishment.setReason(stringValue);

                commonMap.put("attribute", "reason");
                commonMap.put("value", stringValue);

                break;

            case "duration":
                // allow timestamp thingy

                if (!validTimeString(stringValue) && !stringValue.equals("permanent")) {
                    MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_DURATION, Map.of("input", stringValue));
                    return true;
                }

                // If the timeString == "permanent" the duration is -1. We will check if the duration is -1 in the next line, and if it is we pass that as the effectiveUntilTimestamp.
                long duration = stringValue.equalsIgnoreCase("permanent") ? -1 : TimeUtils.toSeconds(stringValue);
                long effectiveUntilTimestamp = duration == -1 ? -1 : punishment.getIssuedTimestamp() + duration;

                punishment.setEffectiveUntilTimestamp(effectiveUntilTimestamp);

                commonMap.put("attribute", "reason");
                commonMap.put("value", TimeUtils.unixCountdownShort(duration));

                break;
        }

        if (!(sender instanceof Player)) {
            punishment.setEditorModUsername("console");
            punishment.setEditorModUUID("console");
        }

        else {
            NetworkPlayer editorMod = NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId());
            punishment.setEditorMod(editorMod);
        }

        punishment.edit(); // To sync with API. This calls {api}/punishment/edit w/ post data
        punishmentIDMap.put(id, punishment);

        commonMap.putAll(MapFormatters.punishmentFormatter(sender, punishment));

        MessageUtils.sendParsedMessage(sender, MessageKey.PUNISHMENT_EDIT, commonMap);

        return true;

    }
}
