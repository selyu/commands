package org.selyu.commands.core.provider.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.selyu.commands.core.argument.CommandArg;
import org.selyu.commands.core.argument.CommandArgs;
import org.selyu.commands.core.provider.IParameterProvider;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class CommandArgsProvider implements IParameterProvider<CommandArgs> {
    @Override
    public boolean consumesArgument() {
        return false;
    }

    @Nullable
    @Override
    public CommandArgs provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) {
        return arg.getArgs();
    }

    @NotNull
    @Override
    public String argumentDescription() {
        return "args";
    }

    @NotNull
    @Override
    public List<String> getSuggestions(@NotNull String input) {
        return Collections.emptyList();
    }
}
