package org.selyu.commands.api.registry;

import org.selyu.commands.api.command.CommandContainer;
import org.selyu.commands.api.exception.CommandRegistrationException;

import javax.annotation.Nonnull;

public interface ICommandRegistry<T extends CommandContainer> {
    boolean register(@Nonnull T container, boolean unregisterExisting) throws CommandRegistrationException;
}
