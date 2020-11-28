package org.selyu.commands.core.command;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.selyu.commands.core.ICommandService;
import org.selyu.commands.core.annotation.Text;
import org.selyu.commands.core.argument.ArgumentParser;
import org.selyu.commands.core.argument.CommandArgs;
import org.selyu.commands.core.exception.*;
import org.selyu.commands.core.executor.ICommandExecutor;
import org.selyu.commands.core.flag.CommandFlag;
import org.selyu.commands.core.flag.FlagExtractor;
import org.selyu.commands.core.help.HelpService;
import org.selyu.commands.core.help.IHelpFormatter;
import org.selyu.commands.core.messages.Messages;
import org.selyu.commands.core.modifier.ICommandModifier;
import org.selyu.commands.core.modifier.ModifierService;
import org.selyu.commands.core.parametric.BindingContainer;
import org.selyu.commands.core.parametric.CommandBinding;
import org.selyu.commands.core.parametric.ProviderAssigner;
import org.selyu.commands.core.parametric.binder.CommandBinder;
import org.selyu.commands.core.preprocessor.ICommandPreProcessor;
import org.selyu.commands.core.preprocessor.ProcessorResult;
import org.selyu.commands.core.provider.IParameterProvider;
import org.selyu.commands.core.provider.impl.*;
import org.selyu.commands.core.util.CommandUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Objects.requireNonNull;

@Getter
public abstract class AbstractCommandService<T extends CommandContainer> implements ICommandService {
    public static String DEFAULT_KEY = "COMMANDS_DEFAULT";

    protected final HelpService helpService = new HelpService(this);
    protected final CommandExtractor extractor = new CommandExtractor(this);
    protected final ProviderAssigner providerAssigner = new ProviderAssigner(this);
    protected final ArgumentParser argumentParser = new ArgumentParser(this);
    protected final ModifierService modifierService = new ModifierService();
    protected final ConcurrentMap<String, T> commands = new ConcurrentHashMap<>();
    protected final ConcurrentMap<Class<?>, BindingContainer<?>> bindings = new ConcurrentHashMap<>();
    protected final Set<ICommandPreProcessor> preProcessors = new HashSet<>();

    public AbstractCommandService() {
        BooleanProvider booleanProvider = new BooleanProvider();
        bind(Boolean.class).toProvider(booleanProvider);
        bind(boolean.class).toProvider(booleanProvider);

        DoubleProvider doubleProvider = new DoubleProvider();
        bind(Double.class).toProvider(doubleProvider);
        bind(double.class).toProvider(doubleProvider);

        IntegerProvider integerProvider = new IntegerProvider();
        bind(Integer.class).toProvider(integerProvider);
        bind(int.class).toProvider(integerProvider);

        LongProvider longProvider = new LongProvider();
        bind(Long.class).toProvider(longProvider);
        bind(long.class).toProvider(longProvider);

        bind(String.class).toProvider(new StringProvider());
        bind(String.class).annotatedWith(Text.class).toProvider(new GreedyStringProvider());
        bind(CommandArgs.class).toProvider(new CommandArgsProvider());
    }

    protected abstract void runAsync(@NotNull Runnable runnable);

    protected abstract void addDefaults();

    @NotNull
    protected abstract T createContainer(@NotNull Object object, @NotNull String name, @NotNull Set<String> aliases, @NotNull Map<String, WrappedCommand> commands);

    @Override
    public void setHelpFormatter(@NotNull IHelpFormatter helpFormatter) {
        requireNonNull(helpFormatter, "HelpFormatter cannot be null");
        helpService.setHelpFormatter(helpFormatter);
    }

    @Override
    public final CommandContainer register(@NotNull Object handler, @NotNull String name, @Nullable String... aliases) throws CommandRegistrationException {
        requireNonNull(handler, "Handler object cannot be null");
        requireNonNull(name, "Name cannot be null");
        CommandUtil.checkState(name.length() > 0, "Name cannot be empty (must be > 0 characters in length)");
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
    public final CommandContainer registerSub(@NotNull CommandContainer root, @NotNull Object handler) {
        requireNonNull(root, "Root command container cannot be null");
        requireNonNull(handler, "Handler object cannot be null");
        try {
            Map<String, WrappedCommand> extractCommands = extractor.extractCommands(handler);
            extractCommands.forEach((s, d) -> root.getCommands().put(s, d));
            return root;
        } catch (MissingProviderException | CommandStructureException ex) {
            throw new CommandRegistrationException("Could not register sub-command in root '" + root + "' with handler '" + handler.getClass().getSimpleName() + "': " + ex.getMessage(), ex);
        }
    }

    @Override
    public final <TYPE> void registerModifier(@NotNull Class<? extends Annotation> annotation, @NotNull Class<TYPE> type, @NotNull ICommandModifier<TYPE> modifier) {
        modifierService.registerModifier(annotation, type, modifier);
    }

    @Override
    public void addPreProcessor(@NotNull ICommandPreProcessor preProcessor) {
        requireNonNull(preProcessor, "preProcessor");
        preProcessors.add(preProcessor);
    }

    public final boolean executeCommand(@NotNull ICommandExecutor<?> sender, @NotNull T container, @NotNull String label, @NotNull String[] args) {
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
                execute(sender, data.getKey(), label, data.getValue());
            } else {
                if (args.length > 0) {
                    if (args[args.length - 1].equalsIgnoreCase("help")) {
                        // Send help if they ask for it, if they registered a custom help sub-command, allow that to override our help menu
                        helpService.sendHelpFor(sender, container);
                        return true;
                    }
                    sender.sendMessage(Messages.format(Messages.unknownSubCommand, args[0], label));
                } else {
                    // Just send help
                    helpService.sendHelpFor(sender, container);
                }
            }
            return true;
        } catch (Exception ex) {
            sender.sendMessage(Messages.exception);
            ex.printStackTrace();
        }

        return false;
    }

    private void execute(@NotNull ICommandExecutor<?> executor, @NotNull WrappedCommand command, @NotNull String label, @NotNull String[] args) {
        requireNonNull(executor, "Sender cannot be null");
        requireNonNull(command, "Command cannot be null");
        requireNonNull(label, "Label cannot be null");
        requireNonNull(args, "Args cannot be null");

        boolean execute = true;
        for (ICommandPreProcessor preProcessor : preProcessors) {
            var result = preProcessor.process(executor, command);
            if (result.equals(ProcessorResult.STOP_EXECUTION)) {
                execute = false;
                break;
            }
        }

        if (execute) {
            if (command.isAsync()) {
                runAsync(() -> finishExecution(executor, command, label, args));
            } else {
                finishExecution(executor, command, label, args);
            }
        }
    }

    private void finishExecution(@NotNull ICommandExecutor<?> sender, @NotNull WrappedCommand command, @NotNull String label, @NotNull String[] args) {
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
            } catch (InvocationTargetException | IllegalAccessException ex) {
                if (ex instanceof InvocationTargetException) {
                    InvocationTargetException ite = (InvocationTargetException) ex;
                    if (ite.getCause() instanceof CommandExitMessage) {
                        CommandExitMessage cem = (CommandExitMessage) ite.getCause();
                        sender.sendMessage(cem.getMessage());
                        if (cem.isShowUsage()) {
                            helpService.sendUsageMessage(sender, getContainerFor(command), command);
                        }
                        return;
                    }
                }
                sender.sendMessage(Messages.exception);
                throw new CommandException("Failed to execute command '" + command.getName() + "' with arguments '" + CommandUtil.join(args, ' ') + " for sender " + sender.getName(), ex);
            }
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(ex.getMessage());
        } catch (CommandArgumentException ex) {
            sender.sendMessage(ex.getMessage());
            helpService.sendUsageMessage(sender, getContainerFor(command), command);
        }
    }

    @Nullable
    public final CommandContainer getContainerFor(@NotNull WrappedCommand command) {
        requireNonNull(command, "WrappedCommand cannot be null");
        for (CommandContainer container : commands.values()) {
            if (container.getCommands().containsValue(command)) {
                return container;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public final <TYPE> BindingContainer<TYPE> getBindingsFor(@NotNull Class<TYPE> type) {
        requireNonNull(type, "Type cannot be null");
        if (bindings.containsKey(type)) {
            return (BindingContainer<TYPE>) bindings.get(type);
        }
        return null;
    }

    @Nullable
    @Override
    public final CommandContainer get(@NotNull String name) {
        requireNonNull(name, "Name cannot be null");
        return commands.get(getCommandKey(name));
    }

    public final String getCommandKey(@NotNull String name) {
        requireNonNull(name, "Name cannot be null");
        if (name.length() == 0) {
            return DEFAULT_KEY;
        }
        return name.toLowerCase();
    }

    @Override
    public final <TYPE> CommandBinder<TYPE> bind(@NotNull Class<TYPE> type) {
        requireNonNull(type, "Type cannot be null for bind");
        return new CommandBinder<>(this, type);
    }

    public final <TYPE> void bindProvider(@NotNull Class<TYPE> type, @NotNull Set<Class<? extends Annotation>> annotations, @NotNull IParameterProvider<TYPE> provider) {
        requireNonNull(type, "Type cannot be null");
        requireNonNull(annotations, "Annotations cannot be null");
        requireNonNull(provider, "Provider cannot be null");
        BindingContainer<TYPE> container = getBindingsFor(type);
        if (container == null) {
            container = new BindingContainer<>(type);
            bindings.put(type, container);
        }
        CommandBinding<TYPE> binding = new CommandBinding<>(type, annotations, provider);
        container.getBindings().add(binding);
    }
}
