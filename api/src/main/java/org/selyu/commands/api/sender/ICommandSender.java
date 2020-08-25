package org.selyu.commands.api.sender;

import javax.annotation.Nonnull;

public interface ICommandSender<T> {
    @Nonnull
    String getName();

    void sendMessage(@Nonnull String message);

    @Nonnull
    T getInstance();
}
