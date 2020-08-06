package com.jonahseguin.drink.spigot;

import com.jonahseguin.drink.api.annotation.Sender;
import com.jonahseguin.drink.api.command.DrinkAuthorizer;
import com.jonahseguin.drink.api.command.DrinkCommandService;
import com.jonahseguin.drink.spigot.authorizer.SpigotAuthorizer;
import com.jonahseguin.drink.spigot.container.SpigotCommandContainer;
import com.jonahseguin.drink.spigot.factory.SpigotCommandContainerFactory;
import com.jonahseguin.drink.spigot.provider.CommandSenderProvider;
import com.jonahseguin.drink.spigot.provider.ConsoleCommandSenderProvider;
import com.jonahseguin.drink.spigot.provider.PlayerProvider;
import com.jonahseguin.drink.spigot.provider.PlayerSenderProvider;
import com.jonahseguin.drink.spigot.registry.SpigotCommandRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;

public final class SpigotCommandService extends DrinkCommandService<SpigotCommandContainer> {
    // TODO: Add a way for each command service to customize messages
    
    private final JavaPlugin plugin;
    private final SpigotCommandRegistry registry;

    public SpigotCommandService(@Nonnull JavaPlugin plugin) {
        super(new SpigotCommandContainerFactory());

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
    protected DrinkAuthorizer<?> getDefaultAuthorizer() {
        return new SpigotAuthorizer();
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
