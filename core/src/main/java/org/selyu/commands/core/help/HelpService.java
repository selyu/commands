package org.selyu.commands.core.help;

import lombok.Getter;
import lombok.Setter;
import org.selyu.commands.core.command.CommandContainer;
import org.selyu.commands.core.command.AbstractCommandService;
import org.selyu.commands.core.command.WrappedCommand;
import org.selyu.commands.core.executor.ICommandExecutor;

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
                sender.sendMessage(container.getName() + (c.getName().length() > 0 ? " " + c.getName() : "") + " " + c.getMostApplicableUsage() + " - " + c.getShortDescription());
            }
            sender.sendMessage("--------------------------------");
        };
    }

    public void sendHelpFor(ICommandExecutor<?> sender, CommandContainer container) {
        this.helpFormatter.sendHelpFor(sender, container);
    }

    public void sendUsageMessage(ICommandExecutor<?> sender, CommandContainer container, WrappedCommand command) {
        sender.sendMessage(getUsageMessage(container, command));
    }

    public String getUsageMessage(CommandContainer container, WrappedCommand command) {
        String usage = commandService.getLang().get("usage_format", container.getName()) + " ";
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
