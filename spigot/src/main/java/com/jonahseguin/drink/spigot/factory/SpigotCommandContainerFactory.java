package com.jonahseguin.drink.spigot.factory;

import com.jonahseguin.drink.api.command.DrinkCommand;
import com.jonahseguin.drink.api.command.DrinkCommandService;
import com.jonahseguin.drink.api.factory.DrinkCommandContainerFactory;
import com.jonahseguin.drink.spigot.container.SpigotCommandContainer;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public final class SpigotCommandContainerFactory implements DrinkCommandContainerFactory<SpigotCommandContainer> {
    @Nonnull
    @Override
    public SpigotCommandContainer create(@Nonnull DrinkCommandService commandService, @Nonnull Object object, @Nonnull String name, @Nonnull Set<String> aliases, @Nonnull Map<String, DrinkCommand> commands) {
        return new SpigotCommandContainer(commandService, object, name, aliases, commands);
    }
}
