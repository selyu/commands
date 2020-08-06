package com.jonahseguin.drink.api.factory;

import com.jonahseguin.drink.api.command.DrinkCommand;
import com.jonahseguin.drink.api.command.DrinkCommandContainer;
import com.jonahseguin.drink.api.command.DrinkCommandService;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public interface DrinkCommandContainerFactory<T extends DrinkCommandContainer> {
    @Nonnull
    T create(@Nonnull DrinkCommandService commandService, @Nonnull Object object, @Nonnull String name, @Nonnull Set<String> aliases, @Nonnull Map<String, DrinkCommand> commands);
}
