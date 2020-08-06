package com.jonahseguin.drink.api.command;

import com.jonahseguin.drink.api.sender.DrinkCommandSender;

import javax.annotation.Nonnull;

public interface HelpFormatter {
    void sendHelpFor(@Nonnull DrinkCommandSender<?> sender, @Nonnull DrinkCommandContainer container);
}
