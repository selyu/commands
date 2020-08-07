package org.selyu.commands.api.command;

import org.selyu.commands.api.argument.CommandArgs;
import org.selyu.commands.api.sender.ICommandSender;
import lombok.Getter;

import java.util.List;

@Getter
public final class CommandExecution {
    private final AbstractCommandService<?> commandService;
    private final ICommandSender<?> sender;
    private final List<String> args;
    private final CommandArgs commandArgs;
    private final WrappedCommand command;
    private boolean canExecute = true;

    public CommandExecution(AbstractCommandService<?> commandService, ICommandSender<?> sender, List<String> args, CommandArgs commandArgs, WrappedCommand command) {
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
