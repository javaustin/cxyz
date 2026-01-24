package com.carrotguy69.cxyz.cmd.admin.level;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.other.utils.ObjectUtils.slice;

public class _LevelExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /level <add | remove | set | view> <amount> [player]
            /level add 50 Steve
        */

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.admin.level";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        String value = "default";
        if (args.length > 0) {
            value = args[0];
        }

        switch (value) {
            case "add":
                new LevelAdd().onCommand(sender, command, s, slice(args, 1));
                break;

            case "remove":
                new LevelRemove().onCommand(sender, command, s, slice(args, 1));
                break;

            case "set":
                new LevelSet().onCommand(sender, command, s, slice(args, 1));
                break;

            default:
                new LevelView().onCommand(sender, command, s, slice(args, 1));
                break;
        }

        return true;
    }

    public static long levelToXP(int level) {
        // Returns the amount of XP required to reach any level.

        /*

        let P = XP
        let L = level

        let P(L) = The total amount of XP required to reach level L from level 0
        let H = XP earned per hour (we think its 255)
        let T = The amount of time (hours) to reach level `L` from level `L-1`
        let C = The amount of time (hours) to reach level `L` from level 0

        P(L) = 30L^2 + 45L {0 <= L}
        T = [P(L) - P(L-1)]/H
        C = P(L)/H
        I(P) = (sqrt(120P + 2025) / 60) - .75

        */

        long value = (long) (30L * Math.pow(level, 2) + 45L * level);

        return level >= 0 ? value : 0;
    }

    public static int xpToLevel(long xp) {
        int value = (int) Math.floor(
                (
                        (Math.sqrt((120 * xp) + 2025))
                        / 60
                ) - .75
        );

        return xp >= 0 ? value : 0;
    }



}
