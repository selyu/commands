package org.selyu.commands.core.help;

import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.command.CommandContainer;
import org.selyu.commands.core.executor.ICommandExecutor;

public interface IHelpFormatter {
    void sendHelpFor(@NotNull ICommandExecutor<?> sender, @NotNull CommandContainer container);
}
