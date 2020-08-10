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
import org.selyu.commands.api.lang.Lang;
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

    protected final CommandExtractor extractor = new CommandExtractor(this);
    protected final HelpService helpService = new HelpService(this);
    protected final ProviderAssigner providerAssigner = new ProviderAssigner(this);
    protected final ArgumentParser argumentParser = new ArgumentParser(this);
    protected final ModifierService modifierService = new ModifierService();
    protected final ConcurrentMap<String, T> commands = new ConcurrentHashMap<>();
    protected final ConcurrentMap<Class<?>, BindingContainer<?>> bindings = new ConcurrentHashMap<>();
    protected final Lang lang = new Lang();
    protected IAuthorizer authorizer = getDefaultAuthorizer();

    public AbstractCommandService() {
        BooleanProvider booleanProvider = new BooleanProvider(lang);
        bind(Boolean.class).toProvider(booleanProvider);
        bind(boolean.class).toProvider(booleanProvider);

        DoubleProvider doubleProvider = new DoubleProvider(lang);
        bind(Double.class).toProvider(doubleProvider);
        bind(double.class).toProvider(doubleProvider);

        IntegerProvider integerProvider = new IntegerProvider(lang);
        bind(Integer.class).toProvider(integerProvider);
        bind(int.class).toProvider(integerProvider);

        LongProvider longProvider = new LongProvider(lang);
        bind(Long.class).toProvider(longProvider);
        bind(long.class).toProvider(longProvider);

        bind(String.class).toProvider(new StringProvider());
        bind(String.class).annotatedWith(Text.class).toProvider(new TextProvider());
        bind(Date.class).toProvider(new DateProvider(lang));
        bind(Date.class).annotatedWith(Duration.class).toProvider(new DurationProvider(lang));
        bind(CommandArgs.class).toProvider(new CommandArgsProvider());

        bindDefaults();
    }

    protected abstract void runAsync(@Nonnull Runnable runnable);

    protected abstract void bindDefaults();

    protected abstract IAuthorizer<?> getDefaultAuthorizer();

    @Nonnull
    protected abstract T createContainer(@Nonnull Object object, @Nonnull String name, @Nonnull Set<String> aliases, @Nonnull Map<String, WrappedCommand> commands);

    @Override
    public final void setAuthorizer(@Nonnull IAuthorizer<?> authorizer) {
        Preconditions.checkNotNull(authorizer, "Authorizer cannot be null");
        this.authorizer = authorizer;
    }

    @Override
    public final CommandContainer register(@Nonnull Object handler, @Nonnull String name, @Nullable String... aliases) throws CommandRegistrationException {
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
            T container = createContainer(handler, name, aliasesSet, extractCommands);
            commands.put(getCommandKey(name), container);
            return container;
        } catch (MissingProviderException | CommandStructureException ex) {
            throw new CommandRegistrationException("Could not register command '" + name + "': " + ex.getMessage(), ex);
        }
    }

    @Override
    public final CommandContainer registerSub(@Nonnull CommandContainer root, @Nonnull Object handler) {
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
    public final <TT> void registerModifier(@Nonnull Class<? extends Annotation> annotation, @Nonnull Class<TT> type, @Nonnull ICommandModifier<TT> modifier) {
        modifierService.registerModifier(annotation, type, modifier);
    }

    public final boolean executeCommand(@Nonnull ICommandSender<?> sender, @Nonnull T container, @Nonnull String label, @Nonnull String[] args) {
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
                    sender.sendMessage(lang.get(Lang.Type.UNKNOWN_SUB_COMMAND, args[0], label));
                } else {
                    if (container.isDefaultCommandIsHelp()) {
                        helpService.sendHelpFor(sender, container);
                    } else {
                        sender.sendMessage(lang.get(Lang.Type.PLEASE_CHOOSE_SUB_COMMAND, label));
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            sender.sendMessage(lang.get(Lang.Type.EXCEPTION));
            ex.printStackTrace();
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private void checkAuthorization(@Nonnull ICommandSender<?> sender, @Nonnull WrappedCommand command, @Nonnull String label, @Nonnull String[] args) {
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
                sender.sendMessage(lang.get(Lang.Type.EXCEPTION));
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
    public final CommandContainer getContainerFor(@Nonnull WrappedCommand command) {
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
    public final <TT> BindingContainer<TT> getBindingsFor(@Nonnull Class<TT> type) {
        Preconditions.checkNotNull(type, "Type cannot be null");
        if (bindings.containsKey(type)) {
            return (BindingContainer<TT>) bindings.get(type);
        }
        return null;
    }

    @Nullable
    @Override
    public final CommandContainer get(@Nonnull String name) {
        Preconditions.checkNotNull(name, "Name cannot be null");
        return commands.get(getCommandKey(name));
    }

    public final String getCommandKey(@Nonnull String name) {
        Preconditions.checkNotNull(name, "Name cannot be null");
        if (name.length() == 0) {
            return DEFAULT_KEY;
        }
        return name.toLowerCase();
    }

    @Override
    public final <TT> CommandBinder<TT> bind(@Nonnull Class<TT> type) {
        Preconditions.checkNotNull(type, "Type cannot be null for bind");
        return new CommandBinder<>(this, type);
    }

    public final <TT> void bindProvider(@Nonnull Class<TT> type, @Nonnull Set<Class<? extends Annotation>> annotations, @Nonnull CommandProvider<TT> provider) {
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
