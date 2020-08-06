package com.jonahseguin.drink.api.registry;

import com.jonahseguin.drink.api.command.DrinkCommandContainer;
import com.jonahseguin.drink.api.exception.CommandRegistrationException;

import javax.annotation.Nonnull;

public interface DrinkCommandRegistry<T extends DrinkCommandContainer> {
    boolean register(@Nonnull T container, boolean unregisterExisting) throws CommandRegistrationException;
}
