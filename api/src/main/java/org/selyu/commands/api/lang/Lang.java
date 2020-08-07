package org.selyu.commands.api.lang;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class Lang {
    private final Map<Type, String> messages = new HashMap<>();

    public Lang() {
        for (Type value : Type.values()) {
            messages.put(value, value.defaultMessage);
        }
    }

    @Nonnull
    public String get(@Nonnull Type type) {
        Preconditions.checkNotNull(type, "Type cannot be null");
        return messages.get(type);
    }

    public void set(@Nonnull Type type, @Nonnull String message) {
        Preconditions.checkNotNull(type, "Type cannot be null");
        Preconditions.checkNotNull(message, "Message cannot be null");
        messages.put(type, message);
    }

    public enum Type {
        EXCEPTION("An exception occurred while performing this command, Please contact an administrator."),
        UNKNOWN_SUB_COMMAND("Unknown sub-command: {0}.  Use '/{1} help' to see available commands."),
        PLEASE_CHOOSE_SUB_COMMAND("Please choose a sub-command.  Use '/{0} help' to see available commands."),

        ;

        private final String defaultMessage;

        Type(@Nonnull String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }
    }
}
