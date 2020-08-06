package org.selyu.commands.api.authorizer;

import org.selyu.commands.api.command.WrappedCommand;
import org.selyu.commands.api.sender.ICommandSender;

import javax.annotation.Nonnull;

public interface IAuthorizer<T> {
    boolean isAuthorized(@Nonnull ICommandSender<T> sender, @Nonnull WrappedCommand command);
}
