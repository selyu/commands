package org.selyu.commands.core.provider.impl;

import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.argument.CommandArg;
import org.selyu.commands.core.lang.Lang;
import org.selyu.commands.core.provider.IParameterProvider;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class LongProvider implements IParameterProvider<Long> {
    private final Lang lang;

    public LongProvider(@NotNull Lang lang) {
        this.lang = lang;
    }

    @Override
    public boolean allowNullArgument() {
        return false;
    }

    @Override
    public Long defaultNullValue() {
        return 0L;
    }

    @Override
    public Long provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws IllegalArgumentException {
        String s = arg.get();
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(lang.get("invalid_long", s));
        }
    }

    @NotNull
    @Override
    public String argumentDescription() {
        return "number";
    }

    @NotNull
    @Override
    public List<String> getSuggestions(@NotNull String input) {
        return Collections.emptyList();
    }
}
