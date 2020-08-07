package org.selyu.commands.api.help;

import org.selyu.commands.api.command.AbstractCommandService;
import org.selyu.commands.api.command.CommandContainer;
import org.selyu.commands.api.command.WrappedCommand;
import org.selyu.commands.api.sender.ICommandSender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class HelpService {
    private final AbstractCommandService<?> commandService;
    private IHelpFormatter helpFormatter;

    public HelpService(AbstractCommandService<?> commandService) {
        this.commandService = commandService;
        this.helpFormatter = (sender, container) -> {
            sender.sendMessage("--------------------------------");
            sender.sendMessage("Help - " + container.getName());
            for (WrappedCommand c : container.getCommands().values()) {
                sender.sendMessage("/" + container.getName() + (c.getName().length() > 0 ? " " + c.getName() : "") + " " + c.getMostApplicableUsage() + " - " + c.getShortDescription());
            }
            sender.sendMessage("--------------------------------");
        };
    }

    public void sendHelpFor(ICommandSender<?> sender, CommandContainer container) {
        this.helpFormatter.sendHelpFor(sender, container);
    }

    public void sendUsageMessage(ICommandSender<?> sender, CommandContainer container, WrappedCommand command) {
        sender.sendMessage(getUsageMessage(container, command));
    }

    public String getUsageMessage(CommandContainer container, WrappedCommand command) {
        String usage = "Command Usage: /" + container.getName() + " ";
        if (command.getName().length() > 0) {
            usage += command.getName() + " ";
        }
        if (command.getUsage() != null && command.getUsage().length() > 0) {
            usage += command.getUsage();
        } else {
            usage += command.getGeneratedUsage();
        }
        return usage;
    }
}
