package org.selyu.commands.spigot;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.annotation.Sender;
import org.selyu.commands.core.command.AbstractCommandService;
import org.selyu.commands.core.command.WrappedCommand;
import org.selyu.commands.core.messages.Messages;
import org.selyu.commands.core.preprocessor.ProcessorResult;
import org.selyu.commands.spigot.annotation.Permission;
import org.selyu.commands.spigot.provider.CommandSenderProvider;
import org.selyu.commands.spigot.provider.ConsoleCommandSenderProvider;
import org.selyu.commands.spigot.provider.PlayerProvider;
import org.selyu.commands.spigot.provider.PlayerSenderProvider;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public final class SpigotCommandService extends AbstractCommandService<SpigotCommandContainer> {
    private final JavaPlugin plugin;
    private final SpigotCommandRegistry registry = new SpigotCommandRegistry(this);

    public SpigotCommandService(@NotNull JavaPlugin plugin) {
        requireNonNull(plugin, "plugin");

        this.plugin = plugin;
        setHelpFormatter((executor, container) -> {
            if (executor.getInstance() instanceof CommandSender) {
                CommandSender sender = (CommandSender) executor.getInstance();
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
    protected void runAsync(@NotNull Runnable runnable) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    @Override
    protected void addDefaults() {
        bind(CommandSender.class).annotatedWith(Sender.class).toProvider(new CommandSenderProvider());
        bind(Player.class).annotatedWith(Sender.class).toProvider(new PlayerSenderProvider());
        bind(ConsoleCommandSender.class).annotatedWith(Sender.class).toProvider(new ConsoleCommandSenderProvider());

        bind(Player.class).toProvider(new PlayerProvider());

        addPreProcessor((executor, command) -> {
            for (Annotation annotation : command.getAnnotations()) {
                if (annotation instanceof Permission) {
                    if (!((CommandSender) executor.getInstance()).hasPermission(((Permission) annotation).value())) {
                        executor.sendMessage(Messages.format(SpigotMessages.noPermission));
                        return ProcessorResult.STOP_EXECUTION;
                    }
                }
            }
            return ProcessorResult.OK;
        });
    }

    @NotNull
    @Override
    protected SpigotCommandContainer createContainer(@NotNull Object object, @NotNull String name, @NotNull Set<String> aliases, @NotNull Map<String, WrappedCommand> commands) {
        return new SpigotCommandContainer(this, object, name, aliases, commands);
    }

    @Override
    public void registerCommands() {
        for (SpigotCommandContainer value : commands.values()) {
            registry.register(value, true);
        }
    }

    @NotNull
    JavaPlugin getPlugin() {
        return plugin;
    }
}
