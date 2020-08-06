package com.jonahseguin.drink.api.sender;

import javax.annotation.Nonnull;

public interface DrinkCommandSender<T> {
    @Nonnull
    String getName();

    void sendMessage(@Nonnull String message);

    boolean hasPermission(@Nonnull String permission);

    @Nonnull
    T getInstance();
}
