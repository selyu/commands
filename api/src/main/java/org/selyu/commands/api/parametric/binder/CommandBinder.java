package org.selyu.commands.api.parametric.binder;

import org.selyu.commands.api.command.AbstractCommandService;
import org.selyu.commands.api.parametric.ICommandProvider;
import org.selyu.commands.api.provider.InstanceProvider;
import org.selyu.commands.api.util.CommandUtil;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public class CommandBinder<T> {
    private final AbstractCommandService<?> commandService;
    private final Class<T> type;
    private final Set<Class<? extends Annotation>> classifiers = new HashSet<>();
    private ICommandProvider<T> provider;

    public CommandBinder(AbstractCommandService<?> commandService, Class<T> type) {
        this.commandService = commandService;
        this.type = type;
    }

    public CommandBinder<T> annotatedWith(@Nonnull Class<? extends Annotation> annotation) {
        CommandUtil.checkState(commandService.getModifierService().isClassifier(annotation), "Annotation " + annotation.getSimpleName() + " must have @Classifer to be bound");
        classifiers.add(annotation);
        return this;
    }

    public void toInstance(@Nonnull T instance) {
        CommandUtil.checkNotNull(instance, "Instance cannot be null for toInstance during binding for " + type.getSimpleName());
        this.provider = new InstanceProvider<>(instance);
        finish();
    }

    public void toProvider(@Nonnull ICommandProvider<T> provider) {
        CommandUtil.checkNotNull(provider, "Provider cannot be null for toProvider during binding for " + type.getSimpleName());
        this.provider = provider;
        finish();
    }

    private void finish() {
        commandService.bindProvider(type, classifiers, provider);
    }
}
