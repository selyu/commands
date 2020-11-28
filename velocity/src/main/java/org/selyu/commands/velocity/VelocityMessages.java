package org.selyu.commands.velocity;

public final class VelocityMessages {
    public static String noPermission = "You don't have permission to execute that command!";
    public static String consoleOnly = "Only console can execute this command!";
    public static String playerOnly = "Only players can execute this command!";

    private VelocityMessages() {
    }

    public static final class Providers {
        /**
         * {0} = Input
         */
        public static String playerNotFound = "Couldn't find player: {0}";
    }
}
