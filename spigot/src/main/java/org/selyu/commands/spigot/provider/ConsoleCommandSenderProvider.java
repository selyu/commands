package org.selyu.commands.spigot.provider;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.selyu.commands.api.argument.CommandArg;
import org.selyu.commands.api.parametric.ICommandProvider;
import org.selyu.commands.spigot.SpigotCommandService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class ConsoleCommandSenderProvider implements ICommandProvider<ConsoleCommandSender> {
    private final SpigotCommandService service;

    public ConsoleCommandSenderProvider(@Nonnull SpigotCommandService service) {
        this.service = service;
    }

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
    public ConsoleCommandSender provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws IllegalArgumentException {
        if (arg.getSender().getInstance() instanceof Player) {
            throw new IllegalArgumentException(service.getLang().get("spigot.console_only_command"));
        }
        return (ConsoleCommandSender) arg.getSender().getInstance();
    }

    @Nonnull
    @Override
    public String argumentDescription() {
        return "console sender";
    }

    @Nonnull
    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }
}