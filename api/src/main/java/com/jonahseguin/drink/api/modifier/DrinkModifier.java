package com.jonahseguin.drink.api.modifier;

import com.jonahseguin.drink.api.parametric.CommandParameter;
import com.jonahseguin.drink.api.command.CommandExecution;
import com.jonahseguin.drink.api.exception.CommandExitMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface DrinkModifier<T> {

    Optional<T> modify(@Nonnull CommandExecution execution, @Nonnull CommandParameter commandParameter, @Nullable T argument) throws CommandExitMessage;

}
