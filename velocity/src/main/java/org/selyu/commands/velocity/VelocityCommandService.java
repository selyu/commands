package org.selyu.commands.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.annotation.Sender;
import org.selyu.commands.core.command.AbstractCommandService;
import org.selyu.commands.core.command.WrappedCommand;
import org.selyu.commands.core.messages.Messages;
import org.selyu.commands.core.preprocessor.ProcessorResult;
import org.selyu.commands.velocity.annotation.Permission;
import org.selyu.commands.velocity.provider.CommandSourceProvider;
import org.selyu.commands.velocity.provider.ConsoleCommandSourceProvider;
import org.selyu.commands.velocity.provider.PlayerCommandSourceProvider;
import org.selyu.commands.velocity.provider.PlayerProvider;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public final class VelocityCommandService extends AbstractCommandService<VelocityCommandContainer> {
    private final Object plugin;
    private final ProxyServer proxyServer;

    public VelocityCommandService(@NotNull Object plugin, @NotNull ProxyServer proxyServer) {
        this.plugin = requireNonNull(plugin, "plugin");
        this.proxyServer = requireNonNull(proxyServer, "proxyServer");
        addDefaults();
        Messages.prefix = '\u00A7' + "c";
    }

    @Override
    protected void runAsync(@NotNull Runnable runnable) {
        proxyServer.getScheduler().buildTask(plugin, runnable).schedule();
    }

    @Override
    protected void addDefaults() {
        bind(CommandSource.class).annotatedWith(Sender.class).toProvider(new CommandSourceProvider());
        bind(ConsoleCommandSource.class).annotatedWith(Sender.class).toProvider(new ConsoleCommandSourceProvider());
        bind(Player.class).annotatedWith(Sender.class).toProvider(new PlayerCommandSourceProvider());

        bind(Player.class).toProvider(new PlayerProvider(proxyServer));

        addPreProcessor((executor, command) -> {
            for (Annotation annotation : command.getAnnotations()) {
                if (annotation instanceof Permission) {
                    if (!((CommandSource) executor.getInstance()).hasPermission(((Permission) annotation).value())) {
                        executor.sendMessage(Messages.format(VelocityMessages.noPermission));
                        return ProcessorResult.STOP_EXECUTION;
                    }
                }
            }
            return ProcessorResult.OK;
        });
    }

    @Override
    protected @NotNull VelocityCommandContainer createContainer(@NotNull Object object, @NotNull String name, @NotNull Set<String> aliases, @NotNull Map<String, WrappedCommand> commands) {
        return new VelocityCommandContainer(this, object, name, aliases, commands);
    }

    @Override
    public void registerCommands() {
        var commandManager = proxyServer.getCommandManager();
        for (VelocityCommandContainer value : commands.values()) {
            commandManager.register(value.getName(), value.new VelocityCommand(this), value.getAliases().toArray(String[]::new));
        }
    }
}
