package com.carrotguy69.cxyz.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Common handling for command-wide flags. */
public final class CommandUtils {
    private static final ThreadLocal<Boolean> SILENT = ThreadLocal.withInitial(() -> false);

    private CommandUtils() {
    }

    /** Removes the silent flag and records it for message helpers used by this command. */
    public static String[] handleSilent(String[] args) {
        boolean silent = List.of(args).contains("-s");
        SILENT.set(silent);

        if (!silent) {
            return args;
        }

        List<String> filtered = new ArrayList<>(Arrays.asList(args));
        filtered.removeIf(arg -> arg.equals("-s"));
        return filtered.toArray(new String[0]);
    }

    public static boolean isSilent() {
        return SILENT.get();
    }

    public static void clearSilent() {
        SILENT.remove();
    }
}
