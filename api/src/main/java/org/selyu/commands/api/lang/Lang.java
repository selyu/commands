package org.selyu.commands.api.lang;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class Lang {
    protected final Map<String, String> messages = new HashMap<>();

    public Lang() {
        for (Messages value : Messages.values()) {
            register(value.key, value.defaultMessage);
        }
    }

    protected final void register(final String key, final String message) {
        requireNonNull(key);
        requireNonNull(message);
        messages.put(key, message);
    }

    public final String get(final String key, final Object... arguments) {
        requireNonNull(key);
        String message = messages.get(key);
        if (message == null) {
            throw new NullPointerException("Invalid key for message: " + key);
        }
        if (arguments.length == 0) {
            return message;
        }
        return new MessageFormat(message).format(arguments);
    }

    public void set(final String key, final String message) {
        requireNonNull(key);
        requireNonNull(message);
        messages.put(key, message);
    }

    protected enum Messages {
        EXCEPTION("exception", "An exception occurred while performing this command, Please contact an administrator."),
        UNKNOWN_SUB_COMMAND("unknown_sub_command", "Unknown sub-command: {0}.  Use '{1} help' to see available commands."),
        PLEASE_CHOOSE_SUB_COMMAND("choose_sub_command", "Please choose a sub-command.  Use '{0} help' to see available commands."),
        NO_PERMISSION("no_permission", "You do not have permission to execute this command."),
        USAGE("usage_format", "Command Usage: {0}"),

        INVALID_BOOLEAN("invalid_boolean", "Required: Boolean (true/false), Given: ''{0}''"),
        INVALID_DOUBLE("invalid_double", "Required: Decimal Number, Given: ''{0}''"),
        INVALID_INTEGER("invalid_integer", "Required: Integer, Given: ''{0}''"),
        INVALID_LONG("invalid_long", "Required: Long Number, Given: ''{0}''"),
        INVALID_ENUM_VALUE("invalid_enum_value", "No matching value found for ''{0}''. Available values: {1}"),
        INVALID_DURATION("invalid_duration", "Duration must be in format hh:mm or hh:mm:ss or 1h2m3s"),
        INVALID_DATE("invalid_date", "Date must be in format: ''{0}''");

        public final String key;
        public final String defaultMessage;

        Messages(final String key, final String defaultMessage) {
            requireNonNull(key);
            requireNonNull(defaultMessage);
            this.key = key;
            this.defaultMessage = defaultMessage;
        }
    }
}
