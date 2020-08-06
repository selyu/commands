package org.selyu.commands.api.argument;

import org.selyu.commands.api.sender.ICommandSender;

public final class CommandArg {
    private final ICommandSender<?> sender;
    private final String value;
    private final String label;
    private final CommandArgs args;

    public CommandArg(ICommandSender<?> sender, String value, CommandArgs args) {
        this.sender = sender;
        this.value = value;
        this.label = args.getLabel();
        this.args = args;
    }

    public ICommandSender<?> getSender() {
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
