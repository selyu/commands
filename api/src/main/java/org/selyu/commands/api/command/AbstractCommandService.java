package org.selyu.commands.api.command;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.selyu.commands.api.ICommandService;
import org.selyu.commands.api.annotation.Duration;
import org.selyu.commands.api.annotation.Text;
import org.selyu.commands.api.argument.ArgumentParser;
import org.selyu.commands.api.argument.CommandArgs;
import org.selyu.commands.api.authorizer.IAuthorizer;
import org.selyu.commands.api.exception.*;
import org.selyu.commands.api.flag.CommandFlag;
import org.selyu.commands.api.flag.FlagExtractor;
import org.selyu.commands.api.help.HelpService;
import org.selyu.commands.api.modifier.ICommandModifier;
import org.selyu.commands.api.modifier.ModifierService;
import org.selyu.commands.api.parametric.BindingContainer;
import org.selyu.commands.api.parametric.CommandBinding;
import org.selyu.commands.api.parametric.CommandProvider;
import org.selyu.commands.api.parametric.ProviderAssigner;
import org.selyu.commands.api.parametric.binder.CommandBinder;
import org.selyu.commands.api.provider.*;
import org.selyu.commands.api.sender.ICommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("rawtypes")
@Getter
public abstract class AbstractCommandService<T extends CommandContainer> implements ICommandService {
    public static String DEFAULT_KEY = "COMMANDS_DEFAULT";

    protected final CommandExtractor extractor;
    protected final HelpService helpService;
    protected final ProviderAssigner providerAssigner;
    protected final ArgumentParser argumentParser;
    protected final ModifierService modifierService;
    protected final ConcurrentMap<String, T> commands = new ConcurrentHashMap<>();
    protected final ConcurrentMap<Class<?>, BindingContainer<?>> bindings = new ConcurrentHashMap<>();
    protected IAuthorizer authorizer;

    public AbstractCommandService() {
        this.extractor = new CommandExtractor(this);
        this.helpService = new HelpService(this);
        this.providerAssigner = new ProviderAssigner(this);
        this.argumentParser = new ArgumentParser(this);
        this.modifierService = new ModifierService();
        authorizer = getDefaultAuthorizer();

        bind(Boolean.class).toProvider(BooleanProvider.INSTANCE);
        bind(boolean.class).toProvider(BooleanProvider.INSTANCE);
        bind(Double.class).toProvider(DoubleProvider.INSTANCE);
        bind(double.class).toProvider(DoubleProvider.INSTANCE);
        bind(Integer.class).toProvider(IntegerProvider.INSTANCE);
        bind(int.class).toProvider(IntegerProvider.INSTANCE);
        bind(Long.class).toProvider(LongProvider.INSTANCE);
        bind(long.class).toProvider(LongProvider.INSTANCE);
        bind(String.class).toProvider(StringProvider.INSTANCE);
        bind(String.class).annotatedWith(Text.class).toProvider(TextProvider.INSTANCE);
        bind(Date.class).toProvider(DateProvider.INSTANCE);
        bind(Date.class).annotatedWith(Duration.class).toProvider(DurationProvider.INSTANCE);
        bind(CommandArgs.class).toProvider(CommandArgsProvider.INSTANCE);

        bindDefaults();
    }

    protected abstract void runAsync(@Nonnull Runnable runnable);

    protected abstract void bindDefaults();

    protected abstract IAuthorizer<?> getDefaultAuthorizer();

    @Nonnull
    protected abstract T createContainer(@Nonnull AbstractCommandService<?> commandService, @Nonnull Object object, @Nonnull String name, @Nonnull Set<String> aliases, @Nonnull Map<String, WrappedCommand> commands);

    @Override
    public void setAuthorizer(@Nonnull IAuthorizer<?> authorizer) {
        Preconditions.checkNotNull(authorizer, "Authorizer cannot be null");
        this.authorizer = authorizer;
    }

    @Override
    public CommandContainer register(@Nonnull Object handler, @Nonnull String name, @Nullable String... aliases) throws CommandRegistrationException {
        Preconditions.checkNotNull(handler, "Handler object cannot be null");
        Preconditions.checkNotNull(name, "Name cannot be null");
        Preconditions.checkState(name.length() > 0, "Name cannot be empty (must be > 0 characters in length)");
        Set<String> aliasesSet = new HashSet<>();
        if (aliases != null) {
            aliasesSet.addAll(Arrays.asList(aliases));
            aliasesSet.removeIf(s -> s.length() == 0);
        }
        try {
            Map<String, WrappedCommand> extractCommands = extractor.extractCommands(handler);
            if (extractCommands.isEmpty()) {
                throw new CommandRegistrationException("There were no commands to register in the " + handler.getClass().getSimpleName() + " class (" + extractCommands.size() + ")");
            }
            T container = createContainer(this, handler, name, aliasesSet, extractCommands);
            commands.put(getCommandKey(name), container);
            return container;
        } catch (MissingProviderException | CommandStructureException ex) {
            throw new CommandRegistrationException("Could not register command '" + name + "': " + ex.getMessage(), ex);
        }
    }

    @Override
    public CommandContainer registerSub(@Nonnull CommandContainer root, @Nonnull Object handler) {
        Preconditions.checkNotNull(root, "Root command container cannot be null");
        Preconditions.checkNotNull(handler, "Handler object cannot be null");
        try {
            Map<String, WrappedCommand> extractCommands = extractor.extractCommands(handler);
            extractCommands.forEach((s, d) -> root.getCommands().put(s, d));
            return root;
        } catch (MissingProviderException | CommandStructureException ex) {
            throw new CommandRegistrationException("Could not register sub-command in root '" + root + "' with handler '" + handler.getClass().getSimpleName() + "': " + ex.getMessage(), ex);
        }
    }

    @Override
    public <TT> void registerModifier(@Nonnull Class<? extends Annotation> annotation, @Nonnull Class<TT> type, @Nonnull ICommandModifier<TT> modifier) {
        modifierService.registerModifier(annotation, type, modifier);
    }

    public boolean executeCommand(@Nonnull ICommandSender<?> sender, @Nonnull T container, @Nonnull String label, @Nonnull String[] args) {
        try {
            Map.Entry<WrappedCommand, String[]> data = container.getCommand(args);
            if (data != null && data.getKey() != null) {
                if (args.length > 0) {
                    if (args[args.length - 1].equalsIgnoreCase("help") && !data.getKey().getName().equalsIgnoreCase("help")) {
                        // Send help if they ask for it, if they registered a custom help sub-command, allow that to override our help menu
                        helpService.sendHelpFor(sender, container);
                        return true;
                    }
                }
                checkAuthorization(sender, data.getKey(), label, data.getValue());
            } else {
                if (args.length > 0) {
                    if (args[args.length - 1].equalsIgnoreCase("help")) {
                        // Send help if they ask for it, if they registered a custom help sub-command, allow that to override our help menu
                        helpService.sendHelpFor(sender, container);
                        return true;
                    }
                    sender.sendMessage("Unknown sub-command: " + args[0] + ".  Use '/" + label + " help' for available commands.");
                } else {
                    if (container.isDefaultCommandIsHelp()) {
                        helpService.sendHelpFor(sender, container);
                    } else {
                        sender.sendMessage("Please choose a sub-command.  Use '/" + label + " help' for available commands.");
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            sender.sendMessage("An exception occurred while performing this command.");
            ex.printStackTrace();
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public void checkAuthorization(@Nonnull ICommandSender<?> sender, @Nonnull WrappedCommand command, @Nonnull String label, @Nonnull String[] args) {
        Preconditions.checkNotNull(sender, "Sender cannot be null");
        Preconditions.checkNotNull(command, "Command cannot be null");
        Preconditions.checkNotNull(label, "Label cannot be null");
        Preconditions.checkNotNull(args, "Args cannot be null");
        if (authorizer.isAuthorized(sender, command)) {
            if (command.isRequiresAsync()) {
                runAsync(() -> finishExecution(sender, command, label, args));
            } else {
                finishExecution(sender, command, label, args);
            }
        }
    }

    private void finishExecution(@Nonnull ICommandSender<?> sender, @Nonnull WrappedCommand command, @Nonnull String label, @Nonnull String[] args) {
        List<String> argList = new ArrayList<>(Arrays.asList(args));
        try {
            argList = argumentParser.combineMultiWordArguments(argList);
            Map<Character, CommandFlag> flags = FlagExtractor.extractFlags(argList);
            final CommandArgs commandArgs = new CommandArgs(this, sender, label, argList, flags);
            CommandExecution execution = new CommandExecution(this, sender, argList, commandArgs, command);
            Object[] parsedArguments = argumentParser.parseArguments(execution, command, commandArgs);
            if (!execution.isCanExecute()) {
                return;
            }
            try {
                command.getMethod().invoke(command.getHandler(), parsedArguments);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                sender.sendMessage("Could not perform command.  Notify an administrator");
                throw new CommandException("Failed to execute command '" + command.getName() + "' with arguments '" + StringUtils.join(Arrays.asList(args), ' ') + " for sender " + sender.getName(), ex);
            }
        } catch (CommandExitMessage ex) {
            sender.sendMessage(ex.getMessage());
        } catch (CommandArgumentException ex) {
            sender.sendMessage(ex.getMessage());
            helpService.sendUsageMessage(sender, getContainerFor(command), command);
        }
    }

    @Nullable
    public CommandContainer getContainerFor(@Nonnull WrappedCommand command) {
        Preconditions.checkNotNull(command, "WrappedCommand cannot be null");
        for (CommandContainer container : commands.values()) {
            if (container.getCommands().containsValue(command)) {
                return container;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <TT> BindingContainer<TT> getBindingsFor(@Nonnull Class<TT> type) {
        Preconditions.checkNotNull(type, "Type cannot be null");
        if (bindings.containsKey(type)) {
            return (BindingContainer<TT>) bindings.get(type);
        }
        return null;
    }

    @Nullable
    @Override
    public CommandContainer get(@Nonnull String name) {
        Preconditions.checkNotNull(name, "Name cannot be null");
        return commands.get(getCommandKey(name));
    }

    public String getCommandKey(@Nonnull String name) {
        Preconditions.checkNotNull(name, "Name cannot be null");
        if (name.length() == 0) {
            return DEFAULT_KEY;
        }
        return name.toLowerCase();
    }

    @Override
    public <TT> CommandBinder<TT> bind(@Nonnull Class<TT> type) {
        Preconditions.checkNotNull(type, "Type cannot be null for bind");
        return new CommandBinder<>(this, type);
    }

    public <TT> void bindProvider(@Nonnull Class<TT> type, @Nonnull Set<Class<? extends Annotation>> annotations, @Nonnull CommandProvider<TT> provider) {
        Preconditions.checkNotNull(type, "Type cannot be null");
        Preconditions.checkNotNull(annotations, "Annotations cannot be null");
        Preconditions.checkNotNull(provider, "Provider cannot be null");
        BindingContainer<TT> container = getBindingsFor(type);
        if (container == null) {
            container = new BindingContainer<>(type);
            bindings.put(type, container);
        }
        CommandBinding<TT> binding = new CommandBinding<>(type, annotations, provider);
        container.getBindings().add(binding);
    }
}
