package org.selyu.commands.spigot;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.selyu.commands.api.command.CommandContainer;
import org.selyu.commands.api.command.CommandService;
import org.selyu.commands.api.command.WrappedCommand;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class SpigotCommandContainer extends CommandContainer {
    SpigotCommandContainer(CommandService<?> commandService, Object object, String name, Set<String> aliases, Map<String, WrappedCommand> commands) {
        super(commandService, object, name, aliases, commands);
    }

    public final class SpigotCommand extends Command implements PluginIdentifiableCommand {
        private final SpigotCommandService commandService;

        public SpigotCommand(@Nonnull SpigotCommandService commandService) {
            super(name, "", "/" + name, new ArrayList<>(aliases));
            this.commandService = commandService;
            if (defaultCommand != null) {
                setUsage("/" + name + " " + defaultCommand.getGeneratedUsage());
                setDescription(defaultCommand.getDescription());
                setPermission(defaultCommand.getPermission());
            }
        }

        @Override
        public boolean execute(CommandSender commandSender, String s, String[] strings) {
            if (getName().equalsIgnoreCase(SpigotCommandContainer.this.getName())) {
                SpigotCommandSender spigotCommandSender = new SpigotCommandSender(commandSender);
                return commandService.executeCommand(spigotCommandSender, SpigotCommandContainer.this, s, strings);
            }
            return false;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
            return tabCompleter.onTabComplete(getName(), args);
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException {
            return tabCompleter.onTabComplete(getName(), args);
        }

        @Override
        public Plugin getPlugin() {
            return commandService.getPlugin();
        }
    }
}
