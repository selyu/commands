package org.selyu.commands.core.tab;

import org.selyu.commands.core.command.CommandContainer;
import org.selyu.commands.core.command.WrappedCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class TabCompleter {
    private final CommandContainer container;

    public TabCompleter(CommandContainer container) {
        this.container = container;
    }

    public List<String> onTabComplete(String commandName, String[] args) {
        if (commandName.equalsIgnoreCase(container.getName())) {
            Map.Entry<WrappedCommand, String[]> data = container.getCommand(args);
            if (data != null && data.getKey() != null) {
                String tabCompleting = "";
                int tabCompletingIndex = 0;
                if (data.getValue().length > 0) {
                    tabCompleting = data.getValue()[data.getValue().length - 1];
                    tabCompletingIndex = data.getValue().length - 1;
                }
                WrappedCommand wrappedCommand = data.getKey();
                if (wrappedCommand.getConsumingProviders().length > tabCompletingIndex) {
                    List<String> s = wrappedCommand.getConsumingProviders()[tabCompletingIndex].getSuggestions(tabCompleting);
                    if (s != null) {
                        List<String> suggestions = new ArrayList<>(s);
                        if (args.length == 0 || args.length == 1) {
                            String tC = "";
                            if (args.length > 0) {
                                tC = args[args.length - 1];
                            }
                            suggestions.addAll(container.getCommandSuggestions(tC));
                        }
                        return suggestions;
                    } else {
                        if (args.length == 0 || args.length == 1) {
                            String tC = "";
                            if (args.length > 0) {
                                tC = args[args.length - 1];
                            }
                            return container.getCommandSuggestions(tC);
                        }
                    }
                } else {
                    if (args.length == 0 || args.length == 1) {
                        String tC = "";
                        if (args.length > 0) {
                            tC = args[args.length - 1];
                        }
                        return container.getCommandSuggestions(tC);
                    }
                }
            } else {
                if (args.length == 0 || args.length == 1) {
                    String tC = "";
                    if (args.length > 0) {
                        tC = args[args.length - 1];
                    }
                    return container.getCommandSuggestions(tC);
                }
            }
        }
        return Collections.emptyList();
    }
}
