package org.selyu.commands.core.command;

import lombok.Getter;
import org.selyu.commands.core.exception.CommandStructureException;
import org.selyu.commands.core.exception.MissingProviderException;
import org.selyu.commands.core.parametric.CommandParameter;
import org.selyu.commands.core.parametric.CommandParameters;
import org.selyu.commands.core.provider.IParameterProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

@Getter
public final class WrappedCommand {
    private final AbstractCommandService<?> commandService;
    private final String name;
    private final Set<String> allAliases;
    private final Set<String> aliases;
    private final String description;
    private final String usage;
    private final Object handler;
    private final Method method;
    private final boolean async;
    private final CommandParameters parameters;
    private final IParameterProvider<?>[] providers;
    private final IParameterProvider<?>[] consumingProviders;
    private final int consumingArgCount;
    private final int requiredArgCount;
    private final String generatedUsage;
    private final Annotation[] annotations;

    public WrappedCommand(AbstractCommandService<?> commandService, String name, Set<String> aliases, String description, String usage, Object handler, Method method, boolean async, Annotation[] annotations) throws MissingProviderException, CommandStructureException {
        this.commandService = commandService;
        this.name = name;
        this.aliases = aliases;
        this.description = description;
        this.usage = usage;
        this.annotations = annotations;
        this.handler = handler;
        this.method = method;
        this.async = async;
        this.parameters = new CommandParameters(method);
        this.providers = commandService.getProviderAssigner().assignProvidersFor(this);
        this.consumingArgCount = calculateConsumingArgCount();
        this.requiredArgCount = calculateRequiredArgCount();
        this.consumingProviders = calculateConsumingProviders();
        this.generatedUsage = generateUsage();
        this.allAliases = aliases;
        if (name.length() > 0 && !name.equals(AbstractCommandService.DEFAULT_KEY)) {
            allAliases.add(name);
        }
    }

    public String getMostApplicableUsage() {
        if (usage.length() > 0) {
            return usage;
        } else {
            return generatedUsage;
        }
    }

    public String getShortDescription() {
        if (description.length() > 24) {
            return description.substring(0, 21) + "...";
        } else {
            return description;
        }
    }

    private String generateUsage() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parameters.getParameters().length; i++) {
            CommandParameter parameter = parameters.getParameters()[i];
            IParameterProvider<?> provider = providers[i];
            if (parameter.isFlag()) {
                sb.append("-").append(parameter.getFlag().value()).append(" ");
            } else {
                if (provider.consumesArgument()) {
                    if (parameter.isOptional()) {
                        sb.append("[").append(provider.argumentDescription());
                        if (parameter.isText()) {
                            sb.append("...");
                        }
                        if (parameter.getDefaultOptionalValue() != null && parameter.getDefaultOptionalValue().length() > 0) {
                            sb.append(" = ").append(parameter.getDefaultOptionalValue());
                        }
                        sb.append("]");
                    } else {
                        sb.append("<").append(provider.argumentDescription());
                        if (parameter.isText()) {
                            sb.append("...");
                        }
                        sb.append(">");
                    }
                    sb.append(" ");
                }
            }
        }
        return sb.toString();
    }

    private IParameterProvider<?>[] calculateConsumingProviders() {
        IParameterProvider<?>[] consumingProviders = new IParameterProvider<?>[consumingArgCount];
        int x = 0;
        for (IParameterProvider<?> provider : providers) {
            if (provider.consumesArgument()) {
                consumingProviders[x] = provider;
                x++;
            }
        }
        return consumingProviders;
    }

    private int calculateConsumingArgCount() {
        int count = 0;
        for (IParameterProvider<?> provider : providers) {
            if (provider.consumesArgument()) {
                count++;
            }
        }
        return count;
    }

    private int calculateRequiredArgCount() {
        int count = 0;
        for (int i = 0; i < parameters.getParameters().length; i++) {
            CommandParameter parameter = parameters.getParameters()[i];
            if (!parameter.isFlag() && !parameter.isOptional()) {
                IParameterProvider<?> provider = providers[i];
                if (provider.consumesArgument()) {
                    count++;
                }
            }
        }
        return count;
    }
}
