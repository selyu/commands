package org.selyu.commands.api.parametric;

import org.selyu.commands.api.argument.CommandArg;
import java.lang.IllegalArgumentException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

public interface ICommandProvider<T> {
    boolean doesConsumeArgument();

    boolean isAsync();

    default boolean allowNullArgument() {
        return true;
    }

    @Nullable
    default T defaultNullValue() {
        return null;
    }

    @Nullable
    T provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws IllegalArgumentException;

    @Nonnull
    String argumentDescription();

    @Nonnull
    List<String> getSuggestions(@Nonnull String prefix);
}
