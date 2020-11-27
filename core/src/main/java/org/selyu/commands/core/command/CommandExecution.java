package org.selyu.commands.core.command;

import lombok.Getter;
import org.selyu.commands.core.argument.CommandArgs;
import org.selyu.commands.core.executor.ICommandExecutor;

import java.util.List;

@Getter
public final class CommandExecution {
    private final AbstractCommandService<?> commandService;
    private final ICommandExecutor<?> sender;
    private final List<String> args;
    private final CommandArgs commandArgs;
    private final WrappedCommand command;
    private boolean canExecute = true;

    public CommandExecution(AbstractCommandService<?> commandService, ICommandExecutor<?> sender, List<String> args, CommandArgs commandArgs, WrappedCommand command) {
        this.commandService = commandService;
        this.sender = sender;
        this.args = args;
        this.commandArgs = commandArgs;
        this.command = command;
    }

    public void preventExecution() {
        canExecute = false;
    }
}
