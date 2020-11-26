package org.selyu.commands.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.selyu.commands.core.annotation.Command;
import org.selyu.commands.core.annotation.Modifier;
import org.selyu.commands.core.command.CommandContainer;
import org.selyu.commands.core.help.IHelpFormatter;
import org.selyu.commands.core.lang.Lang;
import org.selyu.commands.core.modifier.ICommandModifier;
import org.selyu.commands.core.parametric.binder.CommandBinder;
import org.selyu.commands.core.preprocessor.ICommandPreProcessor;
import org.selyu.commands.core.provider.IParameterProvider;

import java.lang.annotation.Annotation;

public interface ICommandService {

    /**
     * Register a command into the Command Service
     *
     * @param handler Object that has the {@link Command} annotated methods
     * @param name    The name of the command to register.
     *                The names of methods within the handler object will be sub-commands to this name.
     *                If you want to create a default command (just name), set the name here and in the
     *                {@link Command} annotation set name = ""
     * @param aliases (Optional) A list of alternate command names that can be used
     * @return The {@link CommandContainer} containing the command you registered
     */
    CommandContainer register(@NotNull Object handler, @NotNull String name, @Nullable String... aliases);

    /**
     * Register a sub-command into the specified root command container
     *
     * @param root    The {@link CommandContainer} super-command to register your sub-commands into
     * @param handler The object that has the {@link Command}
     *                annotated methods to register
     * @return The {@link CommandContainer} containing the command you registered (same as the root passed in)
     */
    CommandContainer registerSub(@NotNull CommandContainer root, @NotNull Object handler);

    /**
     * Must be called after all of you commands have been registered with
     * {@link #register(Object, String, String...)} and {@link #registerSub(CommandContainer, Object)}
     * <p>
     */
    void registerCommands();

    /**
     * Start binding a class type to a {@link IParameterProvider} or instance.
     *
     * @param type The Class type to bind to
     * @param <T>  The type of class
     * @return A {@link CommandBinder} instance to finish the binding
     */
    <T> CommandBinder<T> bind(@NotNull Class<T> type);

    /**
     * Registers a modifier to modify provided arguments for a specific type
     *
     * @param annotation The annotation to use for the modifier (must have {@link Modifier} annotated in it's class)
     * @param type       The type to modify
     * @param modifier   The modifier
     * @param <T>        The type of class to modify
     */
    <T> void registerModifier(@NotNull Class<? extends Annotation> annotation, @NotNull Class<T> type, @NotNull ICommandModifier<T> modifier);

    /**
     * @param name The primary name of the {@link CommandContainer} you want to get
     * @return {@link Nullable} The {@link CommandContainer} with the specified name
     */
    @Nullable
    CommandContainer get(@NotNull String name);

    void addPreProcessor(@NotNull ICommandPreProcessor preProcessor);

    /**
     * Set the help formatter
     *
     * @param helpFormatter The new {@link IHelpFormatter} instance
     */
    void setHelpFormatter(@NotNull IHelpFormatter helpFormatter);

    @NotNull
    Lang getLang();
}
