package com.jonahseguin.drink.api.argument;

import com.jonahseguin.drink.api.sender.DrinkCommandSender;

public class CommandArg {

    private final DrinkCommandSender<?> sender;
    private final String value;
    private final String label;
    private final CommandArgs args;

    public CommandArg(DrinkCommandSender<?> sender, String value, CommandArgs args) {
        this.sender = sender;
        this.value = value;
        this.label = args.getLabel();
        this.args = args;
    }

    public DrinkCommandSender<?> getSender() {
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
