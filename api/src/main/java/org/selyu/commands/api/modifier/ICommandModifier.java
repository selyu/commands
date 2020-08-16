package org.selyu.commands.api.modifier;

import org.selyu.commands.api.parametric.CommandParameter;
import org.selyu.commands.api.command.CommandExecution;
import java.lang.IllegalArgumentException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface ICommandModifier<T> {
    Optional<T> modify(@Nonnull CommandExecution execution, @Nonnull CommandParameter commandParameter, @Nullable T argument) throws IllegalArgumentException;
}
