package com.jonahseguin.drink.api.command;

import com.jonahseguin.drink.api.argument.CommandArgs;
import com.jonahseguin.drink.api.sender.DrinkCommandSender;
import lombok.Getter;

import java.util.List;

@Getter
public class CommandExecution {

    private final DrinkCommandService commandService;
    private final DrinkCommandSender<?> sender;
    private final List<String> args;
    private final CommandArgs commandArgs;
    private final DrinkCommand command;
    private boolean canExecute = true;

    public CommandExecution(DrinkCommandService commandService, DrinkCommandSender<?> sender, List<String> args, CommandArgs commandArgs, DrinkCommand command) {
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
