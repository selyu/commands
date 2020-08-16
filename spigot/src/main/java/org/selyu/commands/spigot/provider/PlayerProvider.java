package org.selyu.commands.spigot.provider;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.selyu.commands.api.argument.CommandArg;
import java.lang.IllegalArgumentException;
import org.selyu.commands.api.parametric.ICommandProvider;
import org.selyu.commands.spigot.SpigotCommandService;
import org.selyu.commands.spigot.lang.SpigotLang;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public final class PlayerProvider implements ICommandProvider<Player> {
    private final SpigotCommandService service;

    public PlayerProvider(@Nonnull SpigotCommandService service) {
        this.service = service;
    }

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Nonnull
    @Override
    public Player provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws IllegalArgumentException {
        String name = arg.get();
        Player p = Bukkit.getServer().getPlayer(name);
        if (p != null) {
            return p;
        }
        throw new IllegalArgumentException(service.getLang().get(SpigotLang.Type.PLAYER_NOT_FOUND, name));
    }

    @Nonnull
    @Override
    public String argumentDescription() {
        return "player";
    }

    @Nonnull
    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Bukkit.getServer().getOnlinePlayers()
                .stream()
                .map(player -> player.getName().toLowerCase())
                .filter(name -> prefix.length() == 0 || name.startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }
}
