package org.selyu.commands.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import org.selyu.commands.core.command.AbstractCommandService;
import org.selyu.commands.core.command.CommandContainer;
import org.selyu.commands.core.command.WrappedCommand;
import org.selyu.commands.core.executor.ICommandExecutor;
import org.selyu.commands.velocity.annotation.Permission;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class VelocityCommandContainer extends CommandContainer {
    public VelocityCommandContainer(AbstractCommandService<?> commandService, Object object, String name, Set<String> aliases, Map<String, WrappedCommand> commands) {
        super(commandService, object, name, aliases, commands);
    }

    private String getPermission() {
        if (defaultCommand == null) {
            return "";
        }

        for (Annotation annotation : defaultCommand.getAnnotations()) {
            if (annotation instanceof Permission) {
                return ((Permission) annotation).value();
            }
        }

        return "";
    }

    public final class VelocityCommand implements SimpleCommand {
        private final VelocityCommandService commandService;

        public VelocityCommand(VelocityCommandService commandService) {
            this.commandService = commandService;
        }

        @Override
        public void execute(Invocation invocation) {
            ICommandExecutor<CommandSource> executor = new VelocityCommandExecutor(invocation.source());
            commandService.executeCommand(executor, VelocityCommandContainer.this, invocation.alias(), invocation.arguments());
        }

        @Override
        public List<String> suggest(Invocation invocation) {
            return tabCompleter.onTabComplete(VelocityCommandContainer.this.getName(), invocation.arguments());
        }

        @Override
        public boolean hasPermission(Invocation invocation) {
            return invocation.source().hasPermission(getPermission());
        }
    }
}
