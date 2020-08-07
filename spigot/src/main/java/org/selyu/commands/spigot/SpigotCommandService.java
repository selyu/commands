package org.selyu.commands.spigot;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.selyu.commands.api.annotation.Sender;
import org.selyu.commands.api.authorizer.IAuthorizer;
import org.selyu.commands.api.command.AbstractCommandService;
import org.selyu.commands.api.command.WrappedCommand;
import org.selyu.commands.spigot.authorizer.SpigotAuthorizer;
import org.selyu.commands.spigot.container.SpigotCommandContainer;
import org.selyu.commands.spigot.provider.CommandSenderProvider;
import org.selyu.commands.spigot.provider.ConsoleCommandSenderProvider;
import org.selyu.commands.spigot.provider.PlayerProvider;
import org.selyu.commands.spigot.provider.PlayerSenderProvider;
import org.selyu.commands.spigot.registry.SpigotCommandRegistry;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public final class SpigotCommandService extends AbstractCommandService<SpigotCommandContainer> {
    // TODO: Add a way for each command service to customize messages

    private final JavaPlugin plugin;
    private final SpigotCommandRegistry registry;

    public SpigotCommandService(@Nonnull JavaPlugin plugin) {
        this.plugin = plugin;
        registry = new SpigotCommandRegistry(this);
    }

    @Override
    protected void runAsync(@Nonnull Runnable runnable) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    @Override
    protected void bindDefaults() {
        bind(CommandSender.class).annotatedWith(Sender.class).toProvider(new CommandSenderProvider());
        bind(Player.class).annotatedWith(Sender.class).toProvider(new PlayerSenderProvider());
        bind(ConsoleCommandSender.class).annotatedWith(Sender.class).toProvider(new ConsoleCommandSenderProvider());

        bind(Player.class).toProvider(new PlayerProvider());
    }

    @Override
    protected IAuthorizer<?> getDefaultAuthorizer() {
        return new SpigotAuthorizer();
    }

    @Nonnull
    @Override
    public SpigotCommandContainer createContainer(@Nonnull AbstractCommandService<?> commandService, @Nonnull Object object, @Nonnull String name, @Nonnull Set<String> aliases, @Nonnull Map<String, WrappedCommand> commands) {
        return new SpigotCommandContainer(commandService, object, name, aliases, commands);
    }

    @Override
    public void registerCommands() {
        for (SpigotCommandContainer value : commands.values()) {
            registry.register(value, true);
        }
    }

    @Nonnull
    public JavaPlugin getPlugin() {
        return plugin;
    }
}
