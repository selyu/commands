package org.selyu.commands.core.parametric.binder;

import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.command.AbstractCommandService;
import org.selyu.commands.core.provider.IParameterProvider;
import org.selyu.commands.core.provider.impl.InstanceProvider;
import org.selyu.commands.core.util.CommandUtil;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CommandBinder<T> {
    private final AbstractCommandService<?> commandService;
    private final Class<T> type;
    private final Set<Class<? extends Annotation>> classifiers = new HashSet<>();
    private IParameterProvider<T> provider;

    public CommandBinder(AbstractCommandService<?> commandService, Class<T> type) {
        this.commandService = commandService;
        this.type = type;
    }

    public CommandBinder<T> annotatedWith(@NotNull Class<? extends Annotation> annotation) {
        CommandUtil.checkState(commandService.getModifierService().isClassifier(annotation), "Annotation " + annotation.getSimpleName() + " must have @Classifer to be bound");
        classifiers.add(annotation);
        return this;
    }

    public void toInstance(@NotNull T instance) {
        Objects.requireNonNull(instance, "Instance cannot be null for toInstance during binding for " + type.getSimpleName());
        this.provider = new InstanceProvider<>(instance);
        finish();
    }

    public void toProvider(@NotNull IParameterProvider<T> provider) {
        Objects.requireNonNull(provider, "Provider cannot be null for toProvider during binding for " + type.getSimpleName());
        this.provider = provider;
        finish();
    }

    private void finish() {
        commandService.bindProvider(type, classifiers, provider);
    }
}
