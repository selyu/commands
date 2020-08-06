package com.jonahseguin.drink.spigot.authorizer;

import com.jonahseguin.drink.api.command.DrinkAuthorizer;
import com.jonahseguin.drink.api.command.DrinkCommand;
import com.jonahseguin.drink.api.sender.DrinkCommandSender;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public final class SpigotAuthorizer implements DrinkAuthorizer<CommandSender> {
    @Override
    public boolean isAuthorized(@Nonnull DrinkCommandSender<CommandSender> sender, @Nonnull DrinkCommand command) {
        if (command.getPermission() != null && command.getPermission().length() > 0) {
            if (!sender.hasPermission(command.getPermission())) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to perform this command.");
                return false;
            }
        }
        return true;
    }
}
