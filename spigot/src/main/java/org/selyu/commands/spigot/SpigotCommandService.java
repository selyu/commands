package org.selyu.commands.spigot;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.selyu.commands.api.annotation.Sender;
import org.selyu.commands.api.command.AbstractCommandService;
import org.selyu.commands.api.command.WrappedCommand;
import org.selyu.commands.spigot.lang.SpigotLang;
import org.selyu.commands.spigot.provider.CommandSenderProvider;
import org.selyu.commands.spigot.provider.ConsoleCommandSenderProvider;
import org.selyu.commands.spigot.provider.PlayerProvider;
import org.selyu.commands.spigot.provider.PlayerSenderProvider;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public final class SpigotCommandService extends AbstractCommandService<SpigotCommandContainer> {
    private final JavaPlugin plugin;
    private final SpigotCommandRegistry registry = new SpigotCommandRegistry(this);
    private final SpigotLang spigotLang = new SpigotLang();

    public SpigotCommandService(@Nonnull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void runAsync(@Nonnull Runnable runnable) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    @Override
    protected void bindDefaults() {
        bind(CommandSender.class).annotatedWith(Sender.class).toProvider(new CommandSenderProvider());
        bind(Player.class).annotatedWith(Sender.class).toProvider(new PlayerSenderProvider(this));
        bind(ConsoleCommandSender.class).annotatedWith(Sender.class).toProvider(new ConsoleCommandSenderProvider(this));

        bind(Player.class).toProvider(new PlayerProvider(this));
    }

    @Nonnull
    @Override
    protected SpigotCommandContainer createContainer(@Nonnull Object object, @Nonnull String name, @Nonnull Set<String> aliases, @Nonnull Map<String, WrappedCommand> commands) {
        return new SpigotCommandContainer(this, object, name, aliases, commands);
    }

    @Override
    public void registerCommands() {
        for (SpigotCommandContainer value : commands.values()) {
            registry.register(value, true);
        }
    }

    @Override
    public SpigotLang getLang() {
        return spigotLang;
    }

    @Nonnull
    JavaPlugin getPlugin() {
        return plugin;
    }
}
