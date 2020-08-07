package org.selyu.commands.api.parametric;

import org.selyu.commands.api.argument.CommandArg;
import org.selyu.commands.api.exception.CommandExitMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

public abstract class CommandProvider<T> {
    public abstract boolean doesConsumeArgument();

    public abstract boolean isAsync();

    public boolean allowNullArgument() {
        return true;
    }

    @Nullable
    public T defaultNullValue() {
        return null;
    }

    @Nullable
    public abstract T provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage;

    public abstract String argumentDescription();

    public abstract List<String> getSuggestions(@Nonnull String prefix);

    protected boolean hasAnnotation(List<? extends Annotation> list, Class<? extends Annotation> a) {
        return list.stream().anyMatch(annotation -> annotation.annotationType().equals(a));
    }
}
