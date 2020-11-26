package org.selyu.commands.core.provider.impl;

import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.argument.CommandArg;
import org.selyu.commands.core.lang.Lang;
import org.selyu.commands.core.provider.IParameterProvider;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class IntegerProvider implements IParameterProvider<Integer> {
    private final Lang lang;

    public IntegerProvider(@NotNull Lang lang) {
        this.lang = lang;
    }

    @Override
    public boolean allowNullArgument() {
        return false;
    }

    @Override
    public Integer defaultNullValue() {
        return 0;
    }

    @Override
    public Integer provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws IllegalArgumentException {
        String s = arg.get();
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(lang.get("invalid_integer", s));
        }
    }

    @NotNull
    @Override
    public String argumentDescription() {
        return "integer";
    }

    @NotNull
    @Override
    public List<String> getSuggestions(@NotNull String input) {
        return Collections.emptyList();
    }
}
