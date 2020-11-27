package org.selyu.commands.core.provider.impl;

import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.argument.CommandArg;
import org.selyu.commands.core.messages.Messages;
import org.selyu.commands.core.provider.IParameterProvider;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class BooleanProvider implements IParameterProvider<Boolean> {
    private static final List<String> SUGGEST = List.of("true", "false");
    private static final List<String> SUGGEST_TRUE = Collections.singletonList("true");
    private static final List<String> SUGGEST_FALSE = Collections.singletonList("false");

    @Override
    public Boolean defaultNullValue() {
        return false;
    }

    @Override
    public Boolean provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws IllegalArgumentException {
        String s = arg.get();
        if (s == null) {
            return false;
        }
        try {
            return Boolean.parseBoolean(s);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(Messages.format(Messages.Providers.invalidBoolean, s));
        }
    }

    @NotNull
    @Override
    public String argumentDescription() {
        return "true/false";
    }

    @NotNull
    @Override
    public List<String> getSuggestions(@NotNull String input) {
        input = input.toLowerCase();
        if (input.length() == 0) {
            return SUGGEST;
        }
        if ("true".startsWith(input)) {
            return SUGGEST_TRUE;
        } else if ("false".startsWith(input)) {
            return SUGGEST_FALSE;
        } else {
            return Collections.emptyList();
        }
    }
}
