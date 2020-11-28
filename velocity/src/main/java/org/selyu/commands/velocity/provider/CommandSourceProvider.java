package org.selyu.commands.velocity.provider;

import com.velocitypowered.api.command.CommandSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.selyu.commands.core.argument.CommandArg;
import org.selyu.commands.core.provider.IParameterProvider;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class CommandSourceProvider implements IParameterProvider<CommandSource> {
    @Override
    public boolean consumesArgument() {
        return false;
    }

    @Override
    public @NotNull CommandSource provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws IllegalArgumentException {
        return (CommandSource) arg.getSender().getInstance();
    }

    @Override
    public @NotNull String argumentDescription() {
        return "sender";
    }

    @Override
    public @NotNull List<String> getSuggestions(@NotNull String input) {
        return Collections.emptyList();
    }
}
