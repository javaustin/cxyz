package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import com.carrotguy69.cxyz.utils.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class PowerTool implements CommandExecutor {

    public static Set<PowerToolEntry> powertools = new HashSet<>();

    public static class PowerToolEntry {
        public enum Type {
            LEFT_CLICK,
            RIGHT_CLICK,
            ANY_CLICK
        }

        public UUID playerUUID;
        public ItemStack itemStack;
        public String commandLine;
        public Type activationType;


        public PowerToolEntry(UUID playerUUID, ItemStack itemStack, String commandLine, Type activationType) {
            this.playerUUID = playerUUID;
            this.itemStack = itemStack;
            this.commandLine = commandLine;
            this.activationType = activationType;
        }

        public static List<PowerToolEntry> getPlayerPowerTools(UUID playerUUID) {
            List<PowerToolEntry> results = new ArrayList<>();

            for (PowerToolEntry entry : powertools) {
                if (entry.playerUUID == playerUUID) {
                    results.add(entry);
                }
            }

            return results;
        }

        public static void removeAll(UUID playerUUID) {
            List<PowerToolEntry> toRemove = new ArrayList<>();


            for (PowerToolEntry entry : powertools) {
                if (entry.playerUUID == playerUUID) {
                    toRemove.add(entry);
                }
            }

            for (PowerToolEntry entry : toRemove) {
                powertools.remove(entry);
            }

            return;
        }

        public static void removeOne(UUID playerUUID, ItemStack item) {
            List<PowerToolEntry> toRemove = new ArrayList<>();


            for (PowerToolEntry entry : powertools) {
                if (entry.playerUUID == playerUUID && Objects.equals(entry.itemStack.getType(), item.getType())) {
                    toRemove.add(entry);
                }
            }

            for (PowerToolEntry entry : toRemove) {
                powertools.remove(entry);
            }

        }

        public void handleClick(Type clickType) {
            if (clickType.equals(activationType) || activationType.equals(Type.ANY_CLICK)) {
                Player p = Bukkit.getPlayer(playerUUID);

                if (p == null)
                    return;

                Bukkit.dispatchCommand(p, commandLine);
            }
        }

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.powertool";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        Player p = (Player) sender;

        NetworkPlayer np = NetworkPlayer.resolvePlayer(p.getUniqueId());

        ItemStack is = p.getInventory().getItemInMainHand();

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);
        commonMap.put("item", is.getType().name());

        if (is.getType() == Material.AIR) {
            MessageUtils.sendParsedMessage(p, MessageKey.INVALID_POWERTOOL_ITEM, commonMap);
            return true;
        }

        PowerToolEntry.Type activationType = PowerToolEntry.Type.ANY_CLICK;

        if (String.join(" ", args).contains("-l")) {
            args = ObjectUtils.removeItem(args, "-l");
            activationType = PowerToolEntry.Type.LEFT_CLICK;
        }

        else if (String.join(" ", args).contains("-r")) {
            args = ObjectUtils.removeItem(args, "-r");
            activationType = PowerToolEntry.Type.RIGHT_CLICK;
        }

        if (args.length == 0) {
            PowerToolEntry.removeOne(p.getUniqueId(), is);

            MessageUtils.sendParsedMessage(p, MessageKey.POWERTOOL_CLEAR, commonMap);
            return true;
        }

        String commandLine = String.join(" ", args);
        commonMap.put("command", commandLine);

        PowerToolEntry entry = new PowerToolEntry(p.getUniqueId(), is, commandLine, activationType);

        powertools.add(entry);

        MessageUtils.sendParsedMessage(p, MessageKey.POWERTOOL, commonMap);
        return true;
    }
}
