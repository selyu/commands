package org.selyu.commands.api.parametric;

import org.selyu.commands.api.command.CommandService;
import org.selyu.commands.api.command.WrappedCommand;
import org.selyu.commands.api.exception.CommandStructureException;
import org.selyu.commands.api.exception.MissingProviderException;

public final class ProviderAssigner {
    private final CommandService<?> commandService;

    public ProviderAssigner(CommandService<?> commandService) {
        this.commandService = commandService;
    }

    public ICommandProvider<?>[] assignProvidersFor(WrappedCommand wrappedCommand) throws MissingProviderException, CommandStructureException {
        CommandParameters parameters = wrappedCommand.getParameters();
        ICommandProvider<?>[] providers = new ICommandProvider<?>[parameters.getParameters().length];
        for (int i = 0; i < parameters.getParameters().length; i++) {
            CommandParameter param = parameters.getParameters()[i];
            if (param.isRequireLastArg() && !parameters.isLastArgument(i)) {
                throw new CommandStructureException("Parameter " + param.getParameter().getName() + " [argument " + i + "] (" + param.getParameter().getType().getSimpleName() + ") in method '" + wrappedCommand.getMethod().getName() + "' must be the last argument in the method.");
            }
            BindingContainer<?> bindings = commandService.getBindingsFor(param.getType());
            if (bindings != null) {
                ICommandProvider<?> provider = null;
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
