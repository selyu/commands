package org.selyu.commands.api.factory;

import org.selyu.commands.api.command.WrappedCommand;
import org.selyu.commands.api.command.CommandContainer;
import org.selyu.commands.api.command.AbstractCommandService;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public interface ICommandContainerFactory<T extends CommandContainer> {
    @Nonnull
    T create(@Nonnull AbstractCommandService<?> commandService, @Nonnull Object object, @Nonnull String name, @Nonnull Set<String> aliases, @Nonnull Map<String, WrappedCommand> commands);
}
