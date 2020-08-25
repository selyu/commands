package org.selyu.commands.spigot;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.selyu.commands.api.annotation.Sender;
import org.selyu.commands.api.command.CommandService;
import org.selyu.commands.api.command.WrappedCommand;
import org.selyu.commands.api.lang.Lang;
import org.selyu.commands.spigot.provider.CommandSenderProvider;
import org.selyu.commands.spigot.provider.ConsoleCommandSenderProvider;
import org.selyu.commands.spigot.provider.PlayerProvider;
import org.selyu.commands.spigot.provider.PlayerSenderProvider;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public final class SpigotCommandService extends CommandService<SpigotCommandContainer> {
    private final JavaPlugin plugin;
    private final SpigotCommandRegistry registry = new SpigotCommandRegistry(this);

    public SpigotCommandService(@Nonnull JavaPlugin plugin) {
        this.plugin = plugin;
        helpService.setHelpFormatter((s, container) -> {
            if (s.getInstance() instanceof CommandSender) {
                CommandSender sender = (CommandSender) s.getInstance();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m--------------------------------"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bHelp &7- &6/" + container.getName()));
                for (WrappedCommand c : container.getCommands().values()) {
                    TextComponent msg = new TextComponent(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&',
                            "&7/" + container.getName() + (c.getName().length() > 0 ? " &e" + c.getName() : "") + " &7" + c.getMostApplicableUsage() + "&7- &f" + c.getShortDescription()));
                    msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GRAY + "/" + container.getName() + " " + c.getName() + "- " + ChatColor.WHITE + c.getDescription())));
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + container.getName() + " " + c.getName()));
                    sender.spigot().sendMessage(msg);
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m--------------------------------"));
            }
        });
    }

    @Override
    protected Lang createLang() {
        return new SpigotLang();
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

    @Nonnull
    JavaPlugin getPlugin() {
        return plugin;
    }
}
