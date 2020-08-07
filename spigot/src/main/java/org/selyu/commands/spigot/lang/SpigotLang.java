package org.selyu.commands.spigot.lang;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.ChatColor;
import org.selyu.commands.api.lang.Lang;

import javax.annotation.Nonnull;
import java.text.MessageFormat;

public final class SpigotLang extends Lang {
    public SpigotLang() {
        for (Type value : Type.values()) {
            messages.put(value, ChatColor.RED + value.defaultMessage);
        }
    }

    @Nonnull
    public String get(@Nonnull Type type, @Nonnull Object... arguments) {
        Preconditions.checkNotNull(type, "Type cannot be null");
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
        Preconditions.checkNotNull(type, "Type cannot be null");
        Preconditions.checkNotNull(message, "Message cannot be null");
        messages.put(type, message);
    }

    public enum Type {
        PLAYER_ONLY_COMMAND("This is a player only command."),
        CONSOLE_ONLY_COMMAND("This is a console only command."),
        PLAYER_NOT_FOUND("The player ''{0}'' isn''t online.");

        private final String defaultMessage;

        Type(@Nonnull String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }
    }
}
