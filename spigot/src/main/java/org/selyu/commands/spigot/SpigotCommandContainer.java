package org.selyu.commands.spigot;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.command.CommandContainer;
import org.selyu.commands.core.command.AbstractCommandService;
import org.selyu.commands.core.command.WrappedCommand;
import org.selyu.commands.spigot.annotation.Permission;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class SpigotCommandContainer extends CommandContainer {
    SpigotCommandContainer(AbstractCommandService<?> commandService, Object object, String name, Set<String> aliases, Map<String, WrappedCommand> commands) {
        super(commandService, object, name, aliases, commands);
    }

    private String getPermission() {
        if (defaultCommand == null) {
            return "";
        }

        for (Annotation annotation : defaultCommand.getAnnotations()) {
            if (annotation instanceof Permission) {
                return ((Permission) annotation).value();
            }
        }

        return "";
    }

    public final class SpigotCommand extends Command implements PluginIdentifiableCommand {
        private final SpigotCommandService commandService;

        public SpigotCommand(@NotNull SpigotCommandService commandService) {
            super(name, "", "/" + name, new ArrayList<>(aliases));
            this.commandService = commandService;
            if (defaultCommand != null) {
                setUsage("/" + name + " " + defaultCommand.getGeneratedUsage());
                setDescription(defaultCommand.getDescription());
                setPermission(SpigotCommandContainer.this.getPermission());
            }
        }

        @Override
        public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, String[] strings) {
            if (getName().equalsIgnoreCase(SpigotCommandContainer.this.getName())) {
                SpigotCommandExecutor spigotCommandSender = new SpigotCommandExecutor(commandSender);
                return commandService.executeCommand(spigotCommandSender, SpigotCommandContainer.this, s, strings);
            }
            return false;
        }

        @Override
        public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
            return tabCompleter.onTabComplete(getName(), args);
        }

        @Override
        public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args, Location location) throws IllegalArgumentException {
            return tabCompleter.onTabComplete(getName(), args);
        }

        @Override
        public @NotNull Plugin getPlugin() {
            return commandService.getPlugin();
        }
    }
}
