package org.selyu.commands.core.authorizer;

import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.command.WrappedCommand;
import org.selyu.commands.core.executor.ICommandExecutor;

public interface IAuthorizer<T> {
    boolean isAuthorized(@NotNull ICommandExecutor<T> sender, @NotNull WrappedCommand command);
}
