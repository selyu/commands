package org.selyu.commands.spigot;

import net.md_5.bungee.api.ChatColor;
import org.selyu.commands.api.lang.Lang;

import static java.util.Objects.requireNonNull;

final class SpigotLang extends Lang {
    public SpigotLang() {
        for (Lang.Messages value : Lang.Messages.values()) {
            set(value.key, ChatColor.RED + value.defaultMessage);
        }
        for (Messages value : Messages.values()) {
            register(value.key, value.defaultMessage);
        }

        set("unknown_sub_command", ChatColor.RED + "Unknown sub-command: {0}.  Use '/{1} help' to see available commands.");
        set("choose_sub_command", ChatColor.RED + "Please choose a sub-command.  Use '/{0} help' to see available commands.");
        set("usage_format", ChatColor.RED + "Command Usage: /{0}");
    }

    private enum Messages {
        PLAYER_ONLY_COMMAND("player_only_command", "This is a player only command."),
        CONSOLE_ONLY_COMMAND("console_only_command", "This is a console only command."),
        PLAYER_NOT_FOUND("player_not_found", "The player ''{0}'' isn''t online.");

        final String key;
        final String defaultMessage;

        Messages(final String key, final String defaultMessage) {
            requireNonNull(key);
            requireNonNull(defaultMessage);
            this.key = "spigot." + key;
            this.defaultMessage = ChatColor.RED + defaultMessage;
        }
    }
}
