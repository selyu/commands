package org.selyu.commands.core.executor;

import org.jetbrains.annotations.NotNull;

public interface ICommandExecutor<T> {
    @NotNull
    String getName();

    void sendMessage(@NotNull String message);

    @NotNull
    T getInstance();
}
