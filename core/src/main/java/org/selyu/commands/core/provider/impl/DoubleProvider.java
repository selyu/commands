package org.selyu.commands.core.provider.impl;

import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.argument.CommandArg;
import org.selyu.commands.core.lang.Lang;
import org.selyu.commands.core.provider.IParameterProvider;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class DoubleProvider implements IParameterProvider<Double> {
    private final Lang lang;

    public DoubleProvider(@NotNull Lang lang) {
        this.lang = lang;
    }

    @Override
    public boolean allowNullArgument() {
        return false;
    }

    @Override
    public Double defaultNullValue() {
        return 0D;
    }

    @Override
    public Double provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws IllegalArgumentException {
        String s = arg.get();
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(lang.get("invalid_double", s));
        }
    }

    @NotNull
    @Override
    public String argumentDescription() {
        return "decimal number";
    }

    @NotNull
    @Override
    public List<String> getSuggestions(@NotNull String input) {
        return Collections.emptyList();
    }
}
