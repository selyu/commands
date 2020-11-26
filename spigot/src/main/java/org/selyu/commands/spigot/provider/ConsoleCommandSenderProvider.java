package org.selyu.commands.spigot.provider;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.selyu.commands.core.argument.CommandArg;
import org.selyu.commands.core.provider.IParameterProvider;
import org.selyu.commands.spigot.SpigotCommandService;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class ConsoleCommandSenderProvider implements IParameterProvider<ConsoleCommandSender> {
    private final SpigotCommandService service;

    public ConsoleCommandSenderProvider(@NotNull SpigotCommandService service) {
        this.service = service;
    }

    @Override
    public boolean consumesArgument() {
        return false;
    }

    @Nullable
    @Override
    public ConsoleCommandSender provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws IllegalArgumentException {
        if (arg.getSender().getInstance() instanceof Player) {
            throw new IllegalArgumentException(service.getLang().get("spigot.console_only_command"));
        }
        return (ConsoleCommandSender) arg.getSender().getInstance();
    }

    @NotNull
    @Override
    public String argumentDescription() {
        return "console sender";
    }

    @NotNull
    @Override
    public List<String> getSuggestions(@NotNull String input) {
        return Collections.emptyList();
    }
}