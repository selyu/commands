package org.selyu.commands.spigot.provider;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.argument.CommandArg;
import org.selyu.commands.core.messages.Messages;
import org.selyu.commands.core.provider.IParameterProvider;
import org.selyu.commands.spigot.SpigotMessages;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class PlayerSenderProvider implements IParameterProvider<Player> {
    @Override
    public boolean consumesArgument() {
        return false;
    }

    @Override
    public @NotNull Player provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws IllegalArgumentException {
        if (arg.getSender().getInstance() instanceof Player) {
            return (Player) arg.getSender().getInstance();
        }
        throw new IllegalArgumentException(Messages.format(SpigotMessages.playerOnly));
    }

    @NotNull
    @Override
    public String argumentDescription() {
        return "player sender";
    }

    @NotNull
    @Override
    public List<String> getSuggestions(@NotNull String input) {
        return Collections.emptyList();
    }
}