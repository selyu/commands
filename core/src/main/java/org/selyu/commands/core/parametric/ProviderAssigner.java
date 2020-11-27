package org.selyu.commands.core.parametric;

import org.selyu.commands.core.command.AbstractCommandService;
import org.selyu.commands.core.command.WrappedCommand;
import org.selyu.commands.core.exception.CommandStructureException;
import org.selyu.commands.core.exception.MissingProviderException;
import org.selyu.commands.core.provider.IParameterProvider;

public final class ProviderAssigner {
    private final AbstractCommandService<?> commandService;

    public ProviderAssigner(AbstractCommandService<?> commandService) {
        this.commandService = commandService;
    }

    public IParameterProvider<?>[] assignProvidersFor(WrappedCommand wrappedCommand) throws MissingProviderException, CommandStructureException {
        CommandParameters parameters = wrappedCommand.getParameters();
        IParameterProvider<?>[] providers = new IParameterProvider<?>[parameters.getParameters().length];
        for (int i = 0; i < parameters.getParameters().length; i++) {
            CommandParameter param = parameters.getParameters()[i];
            if (param.isRequireLastArg() && !parameters.isLastArgument(i)) {
                throw new CommandStructureException("Parameter " + param.getParameter().getName() + " [argument " + i + "] (" + param.getParameter().getType().getSimpleName() + ") in method '" + wrappedCommand.getMethod().getName() + "' must be the last argument in the method.");
            }
            BindingContainer<?> bindings = commandService.getBindingsFor(param.getType());
            if (bindings != null) {
                IParameterProvider<?> provider = null;
                for (CommandBinding<?> binding : bindings.getBindings()) {
                    if (binding.canProvideFor(param)) {
                        provider = binding.getProvider();
                        break;
                    }
                }
                if (provider != null) {
                    providers[i] = provider;
                } else {
                    throw new MissingProviderException("No provider bound for " + param.getType().getSimpleName() + " for parameter " + i + " for method " + wrappedCommand.getMethod().getName());
                }
            } else {
                throw new MissingProviderException("No provider bound for " + param.getType().getSimpleName());
            }
        }
        return providers;
    }
}
