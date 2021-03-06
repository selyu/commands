package org.selyu.commands.spigot.provider;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.argument.CommandArg;
import org.selyu.commands.core.provider.IParameterProvider;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class CommandSenderProvider implements IParameterProvider<CommandSender> {
    @Override
    public boolean consumesArgument() {
        return false;
    }

    @NotNull
    @Override
    public CommandSender provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws IllegalArgumentException {
        return (CommandSender) arg.getSender().getInstance();
    }

    @NotNull
    @Override
    public String argumentDescription() {
        return "sender";
    }

    @NotNull
    @Override
    public List<String> getSuggestions(@NotNull String input) {
        return Collections.emptyList();
    }
}
