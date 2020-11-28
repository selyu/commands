package org.selyu.commands.velocity.provider;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.argument.CommandArg;
import org.selyu.commands.core.messages.Messages;
import org.selyu.commands.core.provider.IParameterProvider;
import org.selyu.commands.velocity.VelocityMessages;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public final class PlayerProvider implements IParameterProvider<Player> {
    private final ProxyServer proxyServer;

    public PlayerProvider(@NotNull ProxyServer proxyServer) {
        requireNonNull(proxyServer, "proxyServer");
        this.proxyServer = proxyServer;
    }

    @Override
    public @NotNull Player provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws IllegalArgumentException {
        var optionalPlayer = proxyServer.getPlayer(arg.get());
        if (optionalPlayer.isPresent()) {
            return optionalPlayer.get();
        }
        throw new IllegalArgumentException(Messages.format(VelocityMessages.Providers.playerNotFound, arg.get()));
    }

    @Override
    public @NotNull String argumentDescription() {
        return "player";
    }

    @Override
    public @NotNull List<String> getSuggestions(@NotNull String input) {
        return proxyServer.getAllPlayers()
                .stream()
                .map(player -> player.getUsername().toLowerCase())
                .filter(name -> input.length() == 0 || name.startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }
}
