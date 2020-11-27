package org.selyu.commands.core.provider.impl;

import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.argument.CommandArg;
import org.selyu.commands.core.provider.IParameterProvider;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class StringProvider implements IParameterProvider<String> {
    @Override
    public String provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) {
        return arg.get();
    }

    @NotNull
    @Override
    public String argumentDescription() {
        return "string";
    }

    @NotNull
    @Override
    public List<String> getSuggestions(@NotNull String input) {
        return Collections.emptyList();
    }
}
