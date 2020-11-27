package org.selyu.commands.core.provider.impl;

import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.argument.CommandArg;
import org.selyu.commands.core.provider.IParameterProvider;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class GreedyStringProvider implements IParameterProvider<String> {
    @Override
    public String provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) {
        StringBuilder builder = new StringBuilder(arg.get());
        while (arg.getArgs().hasNext()) {
            builder.append(" ").append(arg.getArgs().next());
        }
        return builder.toString();
    }

    @NotNull
    @Override
    public String argumentDescription() {
        return "text";
    }

    @NotNull
    @Override
    public List<String> getSuggestions(@NotNull String input) {
        return Collections.emptyList();
    }
}
