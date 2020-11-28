package org.selyu.commands.velocity.provider;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.argument.CommandArg;
import org.selyu.commands.core.provider.IParameterProvider;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class PlayerCommandSourceProvider implements IParameterProvider<Player> {
    @Override
    public boolean consumesArgument() {
        return false;
    }

    @Override
    public @NotNull Player provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws IllegalArgumentException {
        if (!(arg.getSender().getInstance() instanceof Player)) {
            throw new IllegalArgumentException("Players only!");
        }
        return (Player) arg.getSender().getInstance();
    }

    @Override
    public @NotNull String argumentDescription() {
        return "player sender";
    }

    @Override
    public @NotNull List<String> getSuggestions(@NotNull String input) {
        return Collections.emptyList();
    }
}
