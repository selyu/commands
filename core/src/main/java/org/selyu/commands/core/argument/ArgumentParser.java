package org.selyu.commands.core.argument;

import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.annotation.Flag;
import org.selyu.commands.core.command.CommandExecution;
import org.selyu.commands.core.command.AbstractCommandService;
import org.selyu.commands.core.command.WrappedCommand;
import org.selyu.commands.core.exception.CommandArgumentException;
import org.selyu.commands.core.flag.CommandFlag;
import org.selyu.commands.core.parametric.CommandParameter;
import org.selyu.commands.core.provider.IParameterProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ArgumentParser {
    private final AbstractCommandService<?> commandService;

    public ArgumentParser(AbstractCommandService<?> commandService) {
        this.commandService = commandService;
    }

    public List<String> combineMultiWordArguments(List<String> args) {
        List<String> argList = new ArrayList<>(args.size());
        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            if (!arg.isEmpty()) {
                final char c = arg.charAt(0);
                if (c == '"' || c == '\'') {
                    final StringBuilder builder = new StringBuilder();
                    int endIndex;
                    for (endIndex = i; endIndex < args.size(); endIndex++) {
                        final String arg2 = args.get(endIndex);
                        if (arg2.charAt(arg2.length() - 1) == c && arg2.length() > 1) {
                            if (endIndex != i) {
                                builder.append(' ');
                            }
                            builder.append(arg2, endIndex == i ? 1 : 0, arg2.length() - 1);
                            break;
                        } else if (endIndex == i) {
                            builder.append(arg2.substring(1));
                        } else {
                            builder.append(' ').append(arg2);
                        }
                    }
                    if (endIndex < args.size()) {
                        arg = builder.toString();
                        i = endIndex;
                    }
                }
            }
            if (!arg.isEmpty()) {
                argList.add(arg);
            }
        }
        return argList;
    }

    @NotNull
    public Object[] parseArguments(@NotNull CommandExecution execution, @NotNull WrappedCommand command, @NotNull CommandArgs args) throws IllegalArgumentException, CommandArgumentException {
        Objects.requireNonNull(command, "WrappedCommand cannot be null");
        Objects.requireNonNull(args, "CommandArgs cannot be null");
        Object[] arguments = new Object[command.getMethod().getParameterCount()];
        for (int i = 0; i < command.getParameters().getParameters().length; i++) {
            CommandParameter param = command.getParameters().getParameters()[i];
            boolean skipOptional = false; // dont complete exceptionally if true if the arg is missing
            IParameterProvider<?> provider = command.getProviders()[i];
            String value = null;

            if (param.isFlag()) {
                Flag flag = param.getFlag();
                CommandFlag commandFlag = args.getFlags().get(flag.value());
                if (commandFlag != null) {
                    value = commandFlag.getValue();
                } else {
                    value = null;
                }
            } else {
                if (!args.hasNext()) {
                    if (provider.consumesArgument()) {
                        if (param.isOptional()) {
                            String defaultValue = param.getDefaultOptionalValue();
                            if (defaultValue != null && defaultValue.length() > 0) {
                                value = defaultValue;
                            } else {
                                skipOptional = true;
                            }
                        } else {
                            throw new CommandArgumentException("Missing argument for: " + provider.argumentDescription());
                        }
                    }
                }
                if (provider.consumesArgument() && value == null && args.hasNext()) {
                    value = args.next();
                }
                if (provider.consumesArgument() && value == null && !skipOptional) {
                    throw new CommandArgumentException("Argument already consumed for next argument: " + provider.argumentDescription() + " (this is a provider error!)");
                }
            }

            if (param.isFlag() && !param.getType().isAssignableFrom(Boolean.class) && !param.getType().isAssignableFrom(boolean.class)
                    && value == null && !provider.allowNullArgument()) {
                arguments[i] = provider.defaultNullValue();
            } else {
                if (!skipOptional) {
                    Object o = provider.provide(new CommandArg(args.getSender(), value, args), param.getAllAnnotations());
                    o = commandService.getModifierService().executeModifiers(execution, param, o);
                    arguments[i] = o;
                } else {
                    arguments[i] = null;
                }
            }
        }
        return arguments;
    }
}
