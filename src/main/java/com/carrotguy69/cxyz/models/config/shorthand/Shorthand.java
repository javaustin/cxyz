package com.carrotguy69.cxyz.models.config.shorthand;

import com.carrotguy69.cxyz.exceptions.InvalidConfigException;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.models.config.shorthand.interfaces.ShorthandExecutor;
import com.carrotguy69.cxyz.models.config.shorthand.interfaces.ShorthandTabCompleter;
import com.carrotguy69.cxyz.models.config.shorthand.utils.ShorthandUtils;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.other.utils.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Shorthand implements ShorthandExecutor, ShorthandTabCompleter {

    private final String commandName;
    private final String description;
    private final String permission;
    private final String trigger;

    private final List<String> actions;

    public Shorthand(String commandName, String description, String permission, String trigger, List<String> actions) {
        this.commandName = commandName;
        this.description = description;
        this.permission = permission;
        this.trigger = trigger;
        this.actions = actions;
    }

    public String getCommandName() {
        return this.commandName;
    }

    public String getPermission() {
        return this.permission;
    }

    public String getDescription() {
        return this.description;
    }

    public String getTrigger() {
        return this.trigger;
    }

    public List<String> getActions() {
        return this.actions;
    }

    public String[] getTriggerParams() {
        // "example {player} {location}" -> ["{player}", "{location}"]
        return ObjectUtils.slice(trigger.split(" "), 1);
    }

    @Override
    public String toString() {
        return "Shorthand{" +
                "commandName=\"" + commandName + "\"," +
                "description=\"" + description + "\"," +
                "permission=\"" + permission + "\"," +
                "trigger=\"" + trigger + "\"," +
                "actions=" + actions.toString() + "," +
                "}";
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Shorthand shorthand = Shorthand.getShorthandCommand(label);

        if (shorthand == null) {
            Logger.severe("????");
            return false;
        }

        if (!sender.hasPermission(shorthand.getPermission())) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", shorthand.getPermission()));
            return true;
        }

        String[] triggerParams = shorthand.getTriggerParams();

        Map<String, Object> argMap = ShorthandUtils.mapArgs(triggerParams, args);
        argMap.putAll(MapFormatters.cloneFormaterToNewKey(MapFormatters.senderFormatter(sender), "player", "sender"));

        for (String action : shorthand.getActions()) {
            String line = MessageUtils.formatPlaceholders(action, argMap);
            Logger.debugShorthand("shorthand argMap: " + argMap);

            Logger.debugShorthand(String.format("Executing \"%s\" as %s from %s", line, sender.getName(), this));
            Bukkit.dispatchCommand(sender, line);
        }


        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {

        Shorthand shorthand = Shorthand.getShorthandCommand(alias);

        if (shorthand == null) {
            Logger.severe(String.format("Shorthand by name of %s could not be found!", alias));
            return List.of();
        }

        if (!sender.hasPermission(shorthand.getPermission())) {
            Logger.debugShorthand(sender.getName() + String.format(" has no permission to tab complete shorthand \"%s\"", this.commandName));
            return List.of();
        }

        NetworkPlayer np = null;

        if (sender instanceof Player) {
            np = NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId());
        }

        List<String> results = new ArrayList<>();

        String[] params = getTriggerParams();

        int index = Math.max(args.length - 1, 0);

        if (index < params.length) {
            String nonStrippedParam = params[index];

            for (String suggest : ShorthandUtils.getTabSuggestions(nonStrippedParam, sender, np)) {
                if (suggest.toLowerCase().startsWith(args[0].toLowerCase())) {
                    results.add(suggest);
                }
            }

            return results;
        }

        return results;
    }



    public void registerShorthand(String name) {

        Command command = new ShorthandBukkitCommand(
                name,
                this,
                this
        );

        command.setPermission(this.getPermission());
        command.setDescription(this.getDescription());
        command.setUsage(this.getTrigger());


        plugin.commandMap.register(plugin.getName(), command);
    }

    public static Shorthand getShorthandCommand(String name) {
        for (Shorthand cmd : shorthandCommands) {
            if (cmd.getCommandName().equalsIgnoreCase(name))
                return cmd;
        }

        return null;
    }


    public static List<Shorthand> getShorthandCommands() {
        ConfigurationSection section = configYaml.getConfigurationSection("shorthand-commands");

        List<Shorthand> results = new ArrayList<>();

        if (section != null) {
            for (String command : section.getKeys(false)) {
                ConfigurationSection commandData = section.getConfigurationSection(command);

                if (commandData != null) {
                    String description = commandData.getString("description", "A shorthand command");
                    String permission = commandData.getString("permission", "cxyz.shorthand." + command);
                    String trigger = commandData.getString("trigger");
                    List<String> actions = commandData.getStringList("actions");

                    if (trigger == null) {
                        throw new InvalidConfigException("config.yml", String.format("shorthand-commands.%s.trigger", command), String.format("Command trigger for shorthandCommand '%s' cannot be blank!", command));
                    }

                    if (actions.isEmpty()) {
                        Logger.warning(String.format("No actions found for shorthand command %s! Ignoring...", command));
                    }

                    if (!trigger.startsWith(command)) {
                        throw new InvalidConfigException("config.yml", String.format("shorthand-commands.%s.trigger", command), String.format("Command trigger must start with the registered command name (%s)!", command));
                    }

                    Shorthand shortCommand = new Shorthand(command, description, permission, trigger, actions);

                    for (String param : shortCommand.getTriggerParams()) {
                        if (!param.startsWith("{") || !param.endsWith("}")) {
                            throw new InvalidConfigException("config.yml", String.format("shorthand-commands.%s.trigger", command), String.format("Command trigger for shorthandCommand '%s' can only contain bracketed arguments (\"{like-this}\")!", command));
                        }
                    }

                    results.add(shortCommand);

                    shortCommand.registerShorthand(shortCommand.getCommandName());
                }
            }
        }

        return results;
    }

}
