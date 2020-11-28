package org.selyu.commands.velocity.provider;

import com.velocitypowered.api.proxy.ConsoleCommandSource;
import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.argument.CommandArg;
import org.selyu.commands.core.provider.IParameterProvider;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class ConsoleCommandSourceProvider implements IParameterProvider<ConsoleCommandSource> {
    @Override
    public boolean consumesArgument() {
        return false;
    }

    @Override
    public @NotNull ConsoleCommandSource provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws IllegalArgumentException {
        if (!(arg.getSender().getInstance() instanceof ConsoleCommandSource)) {
            throw new IllegalArgumentException("Console only!");
        }
        return (ConsoleCommandSource) arg.getSender().getInstance();
    }

    @Override
    public @NotNull String argumentDescription() {
        return "console sender";
    }

    @Override
    public @NotNull List<String> getSuggestions(@NotNull String input) {
        return Collections.emptyList();
    }
}
