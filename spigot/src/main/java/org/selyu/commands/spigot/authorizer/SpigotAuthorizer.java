package org.selyu.commands.spigot.authorizer;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.selyu.commands.api.authorizer.IAuthorizer;
import org.selyu.commands.api.command.WrappedCommand;
import org.selyu.commands.api.sender.ICommandSender;

import javax.annotation.Nonnull;

public final class SpigotAuthorizer implements IAuthorizer<CommandSender> {
    @Override
    public boolean isAuthorized(@Nonnull ICommandSender<CommandSender> sender, @Nonnull WrappedCommand command) {
        if (command.getPermission() != null && command.getPermission().length() > 0) {
            if (!sender.hasPermission(command.getPermission())) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to perform this command.");
                return false;
            }
        }
        return true;
    }
}
