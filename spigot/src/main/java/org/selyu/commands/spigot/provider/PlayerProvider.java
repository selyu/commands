package org.selyu.commands.spigot.provider;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.argument.CommandArg;
import org.selyu.commands.core.provider.IParameterProvider;
import org.selyu.commands.spigot.SpigotCommandService;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public final class PlayerProvider implements IParameterProvider<Player> {
    private final SpigotCommandService service;

    public PlayerProvider(@NotNull SpigotCommandService service) {
        this.service = service;
    }

    @NotNull
    @Override
    public Player provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws IllegalArgumentException {
        String name = arg.get();
        Player p = Bukkit.getServer().getPlayer(name);
        if (p != null) {
            return p;
        }
        throw new IllegalArgumentException(service.getLang().get("spigot.player_not_found", name));
    }

    @NotNull
    @Override
    public String argumentDescription() {
        return "player";
    }

    @NotNull
    @Override
    public List<String> getSuggestions(@NotNull String input) {
        return Bukkit.getServer().getOnlinePlayers()
                .stream()
                .map(player -> player.getName().toLowerCase())
                .filter(name -> input.length() == 0 || name.startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }
}
