package org.selyu.commands.api.lang;

import org.selyu.commands.api.util.CommandUtil;

import javax.annotation.Nonnull;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class Lang {
    protected final Map<? super Enum<?>, String> messages = new HashMap<>();

    public Lang() {
        for (Type value : Type.values()) {
            messages.put(value, value.defaultMessage);
        }
    }

    @Nonnull
    public String get(@Nonnull Type type, @Nonnull Object... arguments) {
        CommandUtil.checkNotNull(type, "Type cannot be null");
        String message = messages.get(type);
        if (message == null) {
            return "";
        }
        if (arguments.length == 0) {
            return message;
        }
        return new MessageFormat(message).format(arguments);
    }

    public void set(@Nonnull Type type, @Nonnull String message) {
        CommandUtil.checkNotNull(type, "Type cannot be null");
        CommandUtil.checkNotNull(message, "Message cannot be null");
        messages.put(type, message);
    }

    public enum Type {
        EXCEPTION("An exception occurred while performing this command, Please contact an administrator."),
        UNKNOWN_SUB_COMMAND("Unknown sub-command: {0}.  Use '/{1} help' to see available commands."),
        PLEASE_CHOOSE_SUB_COMMAND("Please choose a sub-command.  Use '/{0} help' to see available commands."),
        NO_PERMISSION("You do not have permission to execute this command."),

        INVALID_BOOLEAN("Required: Boolean (true/false), Given: ''{0}''"),
        INVALID_DOUBLE("Required: Decimal Number, Given: ''{0}''"),
        INVALID_INTEGER("Required: Integer, Given: ''{0}''"),
        INVALID_LONG("Required: Long Number, Given: ''{0}''"),
        INVALID_ENUM_VALUE("No matching value found for ''{0}''. Available values: {1}"),
        INVALID_DURATION("Duration must be in format hh:mm or hh:mm:ss or 1h2m3s"),
        INVALID_DATE("Date must be in format: ''{0}''");

        private final String defaultMessage;

        Type(@Nonnull String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }
    }
}
