package org.selyu.commands.core.messages;

import java.text.MessageFormat;

public final class Messages {
    /**
     * Default is an empty string, can be useful for certain platforms.
     */
    public static String prefix = "";

    public static String exception = of("An exception occurred while performing this command, Please contact an administrator.");

    /**
     * {0} = attempted sub-command
     * {1} = root command
     */
    public static String unknownSubCommand = of("Unknown sub-command: {0}.  Use '{1} help' to see available commands.");

    /**
     * {0} = usage
     */
    public static String usageFormat = of("Command Usage: {0}");

    private static String of(String message) {
        return prefix + message;
    }

    public static String format(String message, Object... args) {
        return new MessageFormat(message).format(args);
    }

    public static final class Providers {
        /**
         * {0} = Input
         */
        public static String invalidBoolean = of("Required: Boolean (true/false), Given: ''{0}''");
        /**
         * {0} = Input
         */
        public static String invalidDouble = of("Required: Decimal Number, Given: ''{0}''");
        /**
         * {0} = Input
         */
        public static String invalidInteger = of("Required: Integer, Given: ''{0}''");
        /**
         * {0} = Input
         */
        public static String invalidLong = of("Required: Long Number, Given: ''{0}''");
        /**
         * {0} = Input
         * {1} = Enum name
         * {2} = All enum values
         */
        public static String invalidEnumValue = of("No matching value ''{0}'' found for ''{1}'', Available values: {2}");
    }
}
