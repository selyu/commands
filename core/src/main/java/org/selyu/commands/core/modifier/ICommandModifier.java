package org.selyu.commands.core.modifier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.selyu.commands.core.command.CommandExecution;
import org.selyu.commands.core.parametric.CommandParameter;

import java.util.Optional;

public interface ICommandModifier<T> {
    Optional<T> modify(@NotNull CommandExecution execution, @NotNull CommandParameter commandParameter, @Nullable T argument) throws IllegalArgumentException;
}
