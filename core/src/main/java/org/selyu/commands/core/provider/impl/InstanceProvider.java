package org.selyu.commands.core.provider.impl;

import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.argument.CommandArg;
import org.selyu.commands.core.provider.IParameterProvider;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class InstanceProvider<T> implements IParameterProvider<T> {
    private final T instance;

    public InstanceProvider(T instance) {
        this.instance = instance;
    }

    @Override
    public boolean consumesArgument() {
        return false;
    }

    @Override
    public T provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) {
        return instance;
    }

    @NotNull
    @Override
    public String argumentDescription() {
        return instance.getClass().getSimpleName() + " (provided)";
    }

    @NotNull
    @Override
    public List<String> getSuggestions(@NotNull String input) {
        return Collections.emptyList();
    }
}
