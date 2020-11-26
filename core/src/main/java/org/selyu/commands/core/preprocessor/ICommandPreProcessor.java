package org.selyu.commands.core.preprocessor;

import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.command.WrappedCommand;
import org.selyu.commands.core.executor.ICommandExecutor;

public interface ICommandPreProcessor {
    @NotNull
    ProcessorResult process(@NotNull ICommandExecutor<?> executor, @NotNull WrappedCommand command);
}
