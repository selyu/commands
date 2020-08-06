package com.jonahseguin.drink.api.command;

import com.google.common.base.Preconditions;
import com.jonahseguin.drink.api.CommandService;
import com.jonahseguin.drink.api.annotation.Duration;
import com.jonahseguin.drink.api.annotation.Text;
import com.jonahseguin.drink.api.argument.ArgumentParser;
import com.jonahseguin.drink.api.argument.CommandArgs;
import com.jonahseguin.drink.api.exception.*;
import com.jonahseguin.drink.api.factory.DrinkCommandContainerFactory;
import com.jonahseguin.drink.api.modifier.DrinkModifier;
import com.jonahseguin.drink.api.modifier.ModifierService;
import com.jonahseguin.drink.api.parametric.BindingContainer;
import com.jonahseguin.drink.api.parametric.DrinkBinding;
import com.jonahseguin.drink.api.parametric.DrinkProvider;
import com.jonahseguin.drink.api.parametric.ProviderAssigner;
import com.jonahseguin.drink.api.parametric.binder.DrinkBinder;
import com.jonahseguin.drink.api.provider.*;
import com.jonahseguin.drink.api.sender.DrinkCommandSender;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
public abstract class DrinkCommandService<T extends DrinkCommandContainer> implements CommandService {
    public static String DEFAULT_KEY = "DRINK_DEFAULT";

    private final CommandExtractor extractor;
    private final DrinkHelpService helpService;
    private final ProviderAssigner providerAssigner;
    private final ArgumentParser argumentParser;
    private final ModifierService modifierService;
    private final FlagExtractor flagExtractor;
    protected final ConcurrentMap<String, T> commands = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, BindingContainer<?>> bindings = new ConcurrentHashMap<>();
    private final DrinkCommandContainerFactory<T> commandContainerFactory;
    private DrinkAuthorizer authorizer;

    public DrinkCommandService(@Nonnull DrinkCommandContainerFactory<T> commandContainerFactory) {
        this.commandContainerFactory = commandContainerFactory;
        this.extractor = new CommandExtractor(this);
        this.helpService = new DrinkHelpService(this);
        this.providerAssigner = new ProviderAssigner(this);
        this.argumentParser = new ArgumentParser(this);
        this.modifierService = new ModifierService(this);
        this.flagExtractor = new FlagExtractor(this);
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

    protected abstract DrinkAuthorizer<?> getDefaultAuthorizer();

    @Override
    public void setAuthorizer(@Nonnull DrinkAuthorizer<?> authorizer) {
        Preconditions.checkNotNull(authorizer, "Authorizer cannot be null");
        this.authorizer = authorizer;
    }

    @Override
    public DrinkCommandContainer register(@Nonnull Object handler, @Nonnull String name, @Nullable String... aliases) throws CommandRegistrationException {
        Preconditions.checkNotNull(handler, "Handler object cannot be null");
        Preconditions.checkNotNull(name, "Name cannot be null");
        Preconditions.checkState(name.length() > 0, "Name cannot be empty (must be > 0 characters in length)");
        Set<String> aliasesSet = new HashSet<>();
        if (aliases != null) {
            aliasesSet.addAll(Arrays.asList(aliases));
            aliasesSet.removeIf(s -> s.length() == 0);
        }
        try {
            Map<String, DrinkCommand> extractCommands = extractor.extractCommands(handler);
            if (extractCommands.isEmpty()) {
                throw new CommandRegistrationException("There were no commands to register in the " + handler.getClass().getSimpleName() + " class (" + extractCommands.size() + ")");
            }
            T container = commandContainerFactory.create(this, handler, name, aliasesSet, extractCommands);
            commands.put(getCommandKey(name), container);
            return container;
        } catch (MissingProviderException | CommandStructureException ex) {
            throw new CommandRegistrationException("Could not register command '" + name + "': " + ex.getMessage(), ex);
        }
    }

    @Override
    public DrinkCommandContainer registerSub(@Nonnull DrinkCommandContainer root, @Nonnull Object handler) {
        Preconditions.checkNotNull(root, "Root command container cannot be null");
        Preconditions.checkNotNull(handler, "Handler object cannot be null");
        try {
            Map<String, DrinkCommand> extractCommands = extractor.extractCommands(handler);
            extractCommands.forEach((s, d) -> root.getCommands().put(s, d));
            return root;
        } catch (MissingProviderException | CommandStructureException ex) {
            throw new CommandRegistrationException("Could not register sub-command in root '" + root + "' with handler '" + handler.getClass().getSimpleName() + "': " + ex.getMessage(), ex);
        }
    }

    @Override
    public <TT> void registerModifier(@Nonnull Class<? extends Annotation> annotation, @Nonnull Class<TT> type, @Nonnull DrinkModifier<TT> modifier) {
        modifierService.registerModifier(annotation, type, modifier);
    }

    @SuppressWarnings("unchecked")
    public void executeCommand(@Nonnull DrinkCommandSender<?> sender, @Nonnull DrinkCommand command, @Nonnull String label, @Nonnull String[] args) {
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

    private void finishExecution(@Nonnull DrinkCommandSender<?> sender, @Nonnull DrinkCommand command, @Nonnull String label, @Nonnull String[] args) {
        List<String> argList = new ArrayList<>(Arrays.asList(args));
        try {
            argList = argumentParser.combineMultiWordArguments(argList);
            Map<Character, CommandFlag> flags = flagExtractor.extractFlags(argList);
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
                throw new DrinkException("Failed to execute command '" + command.getName() + "' with arguments '" + StringUtils.join(Arrays.asList(args), ' ') + " for sender " + sender.getName(), ex);
            }
        } catch (CommandExitMessage ex) {
            sender.sendMessage(ex.getMessage());
        } catch (CommandArgumentException ex) {
            sender.sendMessage(ex.getMessage());
            helpService.sendUsageMessage(sender, getContainerFor(command), command);
        }
    }

    @Nullable
    public DrinkCommandContainer getContainerFor(@Nonnull DrinkCommand command) {
        Preconditions.checkNotNull(command, "DrinkCommand cannot be null");
        for (DrinkCommandContainer container : commands.values()) {
            if (container.getCommands().containsValue(command)) {
                return container;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> BindingContainer<T> getBindingsFor(@Nonnull Class<T> type) {
        Preconditions.checkNotNull(type, "Type cannot be null");
        if (bindings.containsKey(type)) {
            return (BindingContainer<T>) bindings.get(type);
        }
        return null;
    }

    @Nullable
    @Override
    public DrinkCommandContainer get(@Nonnull String name) {
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
    public <T> DrinkBinder<T> bind(@Nonnull Class<T> type) {
        Preconditions.checkNotNull(type, "Type cannot be null for bind");
        return new DrinkBinder<>(this, type);
    }

    public <T> void bindProvider(@Nonnull Class<T> type, @Nonnull Set<Class<? extends Annotation>> annotations, @Nonnull DrinkProvider<T> provider) {
        Preconditions.checkNotNull(type, "Type cannot be null");
        Preconditions.checkNotNull(annotations, "Annotations cannot be null");
        Preconditions.checkNotNull(provider, "Provider cannot be null");
        BindingContainer<T> container = getBindingsFor(type);
        if (container == null) {
            container = new BindingContainer<>(type);
            bindings.put(type, container);
        }
        DrinkBinding<T> binding = new DrinkBinding<>(type, annotations, provider);
        container.getBindings().add(binding);
    }
}
