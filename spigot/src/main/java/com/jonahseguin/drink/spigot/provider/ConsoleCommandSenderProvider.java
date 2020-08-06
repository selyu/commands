package com.jonahseguin.drink.spigot.provider;

import com.jonahseguin.drink.api.argument.CommandArg;
import com.jonahseguin.drink.api.exception.CommandExitMessage;
import com.jonahseguin.drink.api.parametric.DrinkProvider;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class ConsoleCommandSenderProvider extends DrinkProvider<ConsoleCommandSender> {
    @Override
    public boolean doesConsumeArgument() {
        return false;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Nullable
    @Override
    public ConsoleCommandSender provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        if (arg.getSender().getInstance() instanceof Player) {
            throw new CommandExitMessage("This is a console-only command.");
        }
        return (ConsoleCommandSender) arg.getSender().getInstance();
    }

    @Override
    public String argumentDescription() {
        return "console sender";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }
}