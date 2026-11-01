package com.carrotguy69.cxyz.utils;

public final class CommandNode {
    public static final String PREFIX = "cxyz.";

    private CommandNode() {
    }

    public static String of(String commandPath) {
        if (commandPath == null) {
            throw new IllegalArgumentException("Command path cannot be null");
        }

        String path = commandPath.trim().toLowerCase();
        while (path.startsWith("/")) {
            path = path.substring(1);
        }

        if (path.startsWith(PREFIX)) {
            return path;
        }

        return PREFIX + path;
    }

    public static String path(String node) {
        String canonical = of(node);
        return canonical.substring(PREFIX.length());
    }
}
