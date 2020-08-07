package org.selyu.commands.api.help;

import org.selyu.commands.api.command.CommandContainer;
import org.selyu.commands.api.sender.ICommandSender;

import javax.annotation.Nonnull;

public interface IHelpFormatter {
    void sendHelpFor(@Nonnull ICommandSender<?> sender, @Nonnull CommandContainer container);
}
