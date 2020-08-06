package org.selyu.commands.spigot.factory;

import org.selyu.commands.api.command.WrappedCommand;
import org.selyu.commands.api.command.AbstractCommandService;
import org.selyu.commands.api.factory.ICommandContainerFactory;
import org.selyu.commands.spigot.container.SpigotCommandContainer;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public final class SpigotCommandContainerFactory implements ICommandContainerFactory<SpigotCommandContainer> {
    @Nonnull
    @Override
    public SpigotCommandContainer create(@Nonnull AbstractCommandService<?> commandService, @Nonnull Object object, @Nonnull String name, @Nonnull Set<String> aliases, @Nonnull Map<String, WrappedCommand> commands) {
        return new SpigotCommandContainer(commandService, object, name, aliases, commands);
    }
}
