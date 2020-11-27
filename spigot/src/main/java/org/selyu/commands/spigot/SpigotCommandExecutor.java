package org.selyu.commands.spigot;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.executor.ICommandExecutor;

final class SpigotCommandExecutor implements ICommandExecutor<CommandSender> {
    private final CommandSender commandSender;

    SpigotCommandExecutor(@NotNull CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    @NotNull
    @Override
    public String getName() {
        return commandSender.getName();
    }

    @Override
    public void sendMessage(@NotNull String message) {
        commandSender.sendMessage(message);
    }

    @NotNull
    @Override
    public CommandSender getInstance() {
        return commandSender;
    }
}
