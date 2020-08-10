package org.selyu.commands.spigot;

import org.bukkit.command.CommandSender;
import org.selyu.commands.api.sender.ICommandSender;

import javax.annotation.Nonnull;

final class SpigotCommandSender implements ICommandSender<CommandSender> {
    private final CommandSender commandSender;

    SpigotCommandSender(@Nonnull CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    @Nonnull
    @Override
    public String getName() {
        return commandSender.getName();
    }

    @Override
    public void sendMessage(@Nonnull String message) {
        commandSender.sendMessage(message);
    }

    @Override
    public boolean hasPermission(@Nonnull String permission) {
        return commandSender.hasPermission(permission);
    }

    @Nonnull
    @Override
    public CommandSender getInstance() {
        return commandSender;
    }
}
