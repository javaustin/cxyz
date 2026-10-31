package com.carrotguy69.cxyz.cmd.cosmetic;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.carrotguy69.cxyz.CXYZ.cosmeticsYML;
import static com.carrotguy69.cxyz.CXYZ.enabledCosmeticTypes;
import static com.carrotguy69.cxyz.CXYZ.plugin;

public class CosmeticDisable implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {

        /*
        SYNTAX:
            /cosmetic disable [*type]
        */

        if (CommandRestrictor.handleRestricted("cosmetic.disable", sender))
            return true;

        String node = "cxyz.cosmetic.disable";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (args.length == 0 || args[0].equals("*")) {
            // Disabled all cosmetic types
            disableCosmetics(enabledCosmeticTypes);
            return true;
        }

        List<Cosmetic.CosmeticType> matchingTypes = new ArrayList<>();

        for (String arg : args) {
            try {
                Cosmetic.CosmeticType type = Cosmetic.CosmeticType.valueOf(arg.toUpperCase());
                matchingTypes.add(type);
            }
            catch (IllegalArgumentException e) {
                MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_COSMETIC_TYPE, Map.of("input", arg));
                return true;
            }
        }

        List<String> stringResults = disableCosmetics(matchingTypes);

        MessageUtils.sendParsedMessage(sender, MessageKey.COSMETIC_DISABLE, Map.of("enabled-types", String.join(" ", stringResults)));

        return true;
    }

    public static List<String> disableCosmetics(List<Cosmetic.CosmeticType> matchingTypes) {

        List<Cosmetic.CosmeticType> enabledTypes = new ArrayList<>(enabledCosmeticTypes);

        enabledTypes.removeIf(matchingTypes::contains);

        CXYZ.cosmeticsYML.set("enabled-types", enabledTypes);

        File file = new File(plugin.getDataFolder(), "cosmetics.yml");
        try {
            cosmeticsYML.save(file);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        cosmeticsYML = YamlConfiguration.loadConfiguration(file);

        enabledCosmeticTypes = cosmeticsYML.getStringList("enabled-types").stream().map(Cosmetic.CosmeticType::valueOf).collect(Collectors.toList());

        unequipOfType(matchingTypes);

        return matchingTypes.stream().map(Cosmetic.CosmeticType::name).toList();
    }

    public static void unequipOfType(List<Cosmetic.CosmeticType> matchingTypes) {
        List<NetworkPlayer> online = Bukkit.getOnlinePlayers().stream().map(p -> NetworkPlayer.resolvePlayer(p.getUniqueId())).toList();

        for (NetworkPlayer np : online) {
            for (Cosmetic.CosmeticType type : matchingTypes) {
                np.unEquipCosmeticOfType(type, false);
            }
        }
    }
}
