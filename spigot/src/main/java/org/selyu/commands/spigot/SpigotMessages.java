package org.selyu.commands.spigot;

import org.selyu.commands.core.messages.Messages;

public final class SpigotMessages {
    public static String noPermission = of("You don't have permission to execute that command!");
    public static String consoleOnly = of("Only console can execute this command!");
    public static String playerOnly = of("Only players can execute this command!");

    private static String of(String message) {
        return Messages.prefix + message;
    }

    public static final class Providers {
        /**
         * {0} = Input
         */
        public static String playerNotFound = of("Couldn't find player: {0}");
    }
}
