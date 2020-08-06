package com.jonahseguin.drink.api.command;

import com.jonahseguin.drink.api.sender.DrinkCommandSender;

import javax.annotation.Nonnull;

public interface DrinkAuthorizer<T> {
    boolean isAuthorized(@Nonnull DrinkCommandSender<T> sender, @Nonnull DrinkCommand command);
}
