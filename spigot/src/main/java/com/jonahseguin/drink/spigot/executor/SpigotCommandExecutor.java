package com.jonahseguin.drink.spigot.executor;

import com.jonahseguin.drink.api.command.DrinkCommand;
import com.jonahseguin.drink.spigot.SpigotCommandService;
import com.jonahseguin.drink.spigot.container.SpigotCommandContainer;
import com.jonahseguin.drink.spigot.sender.SpigotCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.Map;

public final class SpigotCommandExecutor implements CommandExecutor {
    private final SpigotCommandService commandService;
    private final SpigotCommandContainer container;

    public SpigotCommandExecutor(@Nonnull SpigotCommandService commandService, @Nonnull SpigotCommandContainer container) {
        this.commandService = commandService;
        this.container = container;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase(container.getName())) {
            SpigotCommandSender spigotCommandSender = new SpigotCommandSender(sender);
            try {
                Map.Entry<DrinkCommand, String[]> data = container.getCommand(args);
                if (data != null && data.getKey() != null) {
                    if (args.length > 0) {
                        if (args[args.length - 1].equalsIgnoreCase("help") && !data.getKey().getName().equalsIgnoreCase("help")) {
                            // Send help if they ask for it, if they registered a custom help sub-command, allow that to override our help menu
                            commandService.getHelpService().sendHelpFor(spigotCommandSender, container);
                            return true;
                        }
                    }
                    commandService.executeCommand(spigotCommandSender, data.getKey(), label, data.getValue());
                } else {
                    if (args.length > 0) {
                        if (args[args.length - 1].equalsIgnoreCase("help")) {
                            // Send help if they ask for it, if they registered a custom help sub-command, allow that to override our help menu
                            commandService.getHelpService().sendHelpFor(spigotCommandSender, container);
                            return true;
                        }
                        sender.sendMessage(ChatColor.RED + "Unknown sub-command: " + args[0] + ".  Use '/" + label + " help' for available commands.");
                    } else {
                        if (container.isDefaultCommandIsHelp()) {
                            commandService.getHelpService().sendHelpFor(spigotCommandSender, container);
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "Please choose a sub-command.  Use '/" + label + " help' for available commands.");
                        }
                    }
                }
                return true;
            }
            catch (Exception ex) {
                sender.sendMessage(ChatColor.RED + "An exception occurred while performing this command.");
                ex.printStackTrace();
            }
        }
        return false;
    }
}
