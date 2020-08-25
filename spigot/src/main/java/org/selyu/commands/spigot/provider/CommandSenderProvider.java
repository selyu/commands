package org.selyu.commands.spigot.provider;

import org.bukkit.command.CommandSender;
import org.selyu.commands.api.argument.CommandArg;
import org.selyu.commands.api.parametric.ICommandProvider;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class CommandSenderProvider implements ICommandProvider<CommandSender> {
    @Override
    public boolean doesConsumeArgument() {
        return false;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean allowNullArgument() {
        return true;
    }

    @Nonnull
    @Override
    public CommandSender provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws IllegalArgumentException {
        return (CommandSender) arg.getSender().getInstance();
    }

    @Nonnull
    @Override
    public String argumentDescription() {
        return "sender";
    }

    @Nonnull
    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }
}
