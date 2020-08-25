package org.selyu.commands.api.command;

import org.selyu.commands.api.annotation.Command;
import org.selyu.commands.api.exception.CommandRegistrationException;
import org.selyu.commands.api.exception.CommandStructureException;
import org.selyu.commands.api.exception.MissingProviderException;
import org.selyu.commands.api.util.CommandUtil;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.*;

public final class CommandExtractor {
    private final CommandService<?> commandService;

    public CommandExtractor(CommandService<?> commandService) {
        this.commandService = commandService;
    }

    public Map<String, WrappedCommand> extractCommands(@Nonnull Object handler) throws MissingProviderException, CommandStructureException {
        CommandUtil.checkNotNull(handler, "Handler object cannot be null");
        final Map<String, WrappedCommand> commands = new HashMap<>();
        for (Method method : handler.getClass().getDeclaredMethods()) {
            Optional<WrappedCommand> o = extractCommand(handler, method);
            if (o.isPresent()) {
                WrappedCommand wrappedCommand = o.get();
                commands.put(commandService.getCommandKey(wrappedCommand.getName()), wrappedCommand);
            }
        }
        return commands;
    }

    private Optional<WrappedCommand> extractCommand(@Nonnull Object handler, @Nonnull Method method) throws MissingProviderException, CommandStructureException {
        CommandUtil.checkNotNull(handler, "Handler object cannot be null");
        CommandUtil.checkNotNull(method, "Method cannot be null");
        if (method.isAnnotationPresent(Command.class)) {
            try {
                method.setAccessible(true);
            } catch (SecurityException ex) {
                throw new CommandRegistrationException("Couldn't access method " + method.getName());
            }
            Command command = method.getAnnotation(Command.class);
            WrappedCommand wrappedCommand = new WrappedCommand(
                    commandService, command.name(), new HashSet<>(Arrays.asList(command.aliases())),
                    command.desc(), command.usage(), handler, method, command.async(), method.getDeclaredAnnotations()
            );
            return Optional.of(wrappedCommand);
        }
        return Optional.empty();
    }
}
