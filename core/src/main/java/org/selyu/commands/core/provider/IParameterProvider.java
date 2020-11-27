package org.selyu.commands.core.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.selyu.commands.core.argument.CommandArg;

import java.lang.annotation.Annotation;
import java.util.List;

public interface IParameterProvider<T> {
    default boolean consumesArgument() {
        return true;
    }

    default boolean allowNullArgument() {
        return true;
    }

    @Nullable
    default T defaultNullValue() {
        return null;
    }

    @Nullable
    T provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws IllegalArgumentException;

    @NotNull
    String argumentDescription();

    @NotNull
    List<String> getSuggestions(@NotNull String input);
}
