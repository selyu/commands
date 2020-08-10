package org.selyu.commands.api.command;

import com.google.common.base.Preconditions;
import org.selyu.commands.api.tab.TabCompleter;
import org.selyu.commands.api.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public abstract class CommandContainer {
    protected final AbstractCommandService<?> commandService;
    protected final Object object;
    protected final String name;
    protected final Set<String> aliases;
    protected final Map<String, WrappedCommand> commands;
    protected final WrappedCommand defaultCommand;
    protected final TabCompleter tabCompleter;
    protected boolean overrideExistingCommands = true;
    protected boolean defaultCommandIsHelp = false;

    public CommandContainer(AbstractCommandService<?> commandService, Object object, String name, Set<String> aliases, Map<String, WrappedCommand> commands) {
        this.commandService = commandService;
        this.object = object;
        this.name = name;
        this.aliases = aliases;
        this.commands = commands;
        this.defaultCommand = calculateDefaultCommand();
        this.tabCompleter = new TabCompleter(this);
    }

    public final CommandContainer registerSub(@Nonnull Object handler) {
        return commandService.registerSub(this, handler);
    }

    public List<String> getCommandSuggestions(@Nonnull String prefix) {
        Preconditions.checkNotNull(prefix, "Prefix cannot be null");
        final String p = prefix.toLowerCase();
        List<String> suggestions = new ArrayList<>();
        for (WrappedCommand c : commands.values()) {
            for (String alias : c.getAllAliases()) {
                if (alias.length() > 0) {
                    if (p.length() == 0 || alias.toLowerCase().startsWith(p)) {
                        suggestions.add(alias);
                    }
                }
            }
        }
        return suggestions;
    }

    private WrappedCommand calculateDefaultCommand() {
        for (WrappedCommand dc : commands.values()) {
            if (dc.getName().length() == 0 || dc.getName().equals(AbstractCommandService.DEFAULT_KEY)) {
                // assume default!
                return dc;
            }
        }
        return null;
    }

    @Nullable
    public WrappedCommand get(@Nonnull String name) {
        Preconditions.checkNotNull(name, "Name cannot be null");
        return commands.get(commandService.getCommandKey(name));
    }

    @Nullable
    public WrappedCommand getByKeyOrAlias(@Nonnull String key) {
        Preconditions.checkNotNull(key, "Key cannot be null");
        if (commands.containsKey(key)) {
            return commands.get(key);
        }
        for (WrappedCommand wrappedCommand : commands.values()) {
            if (wrappedCommand.getAliases().contains(key)) {
                return wrappedCommand;
            }
        }
        return null;
    }

    /**
     * Gets a sub-command based on given arguments and also returns the new actual argument values
     * based on the arguments that were consumed for the sub-command key
     *
     * @param args the original arguments passed in
     * @return the WrappedCommand (if present, Nullable) and the new argument array
     */
    @Nullable
    public Map.Entry<WrappedCommand, String[]> getCommand(String[] args) {
        for (int i = (args.length - 1); i >= 0; i--) {
            String key = commandService.getCommandKey(StringUtils.join(Arrays.copyOfRange(args, 0, i + 1), ' '));
            WrappedCommand wrappedCommand = getByKeyOrAlias(key);
            if (wrappedCommand != null) {
                return new AbstractMap.SimpleEntry<>(wrappedCommand, Arrays.copyOfRange(args, i + 1, args.length));
            }
        }
        return new AbstractMap.SimpleEntry<>(getDefaultCommand(), args);
    }

    @Nullable
    public WrappedCommand getDefaultCommand() {
        return defaultCommand;
    }

    public AbstractCommandService<?> getCommandService() {
        return commandService;
    }

    public Object getObject() {
        return object;
    }

    public String getName() {
        return name;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public Map<String, WrappedCommand> getCommands() {
        return commands;
    }

    public TabCompleter getTabCompleter() {
        return tabCompleter;
    }

    public boolean isOverrideExistingCommands() {
        return overrideExistingCommands;
    }

    public CommandContainer setOverrideExistingCommands(boolean overrideExistingCommands) {
        this.overrideExistingCommands = overrideExistingCommands;
        return this;
    }

    public boolean isDefaultCommandIsHelp() {
        return defaultCommandIsHelp;
    }

    public CommandContainer setDefaultCommandIsHelp(boolean defaultCommandIsHelp) {
        this.defaultCommandIsHelp = defaultCommandIsHelp;
        return this;
    }
}
