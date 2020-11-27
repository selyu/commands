package org.selyu.commands.core.argument;

import org.selyu.commands.core.executor.ICommandExecutor;

public final class CommandArg {
    private final ICommandExecutor<?> sender;
    private final String value;
    private final String label;
    private final CommandArgs args;

    public CommandArg(ICommandExecutor<?> sender, String value, CommandArgs args) {
        this.sender = sender;
        this.value = value;
        this.label = args.getLabel();
        this.args = args;
    }

    public ICommandExecutor<?> getSender() {
        return sender;
    }

    public String get() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public CommandArgs getArgs() {
        return args;
    }
}
