package org.selyu.commands.api.modifier;

import com.google.common.base.Preconditions;
import org.selyu.commands.api.annotation.Classifier;
import org.selyu.commands.api.annotation.Modifier;
import org.selyu.commands.api.command.CommandExecution;
import org.selyu.commands.api.exception.CommandExitMessage;
import org.selyu.commands.api.parametric.CommandParameter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ModifierService {
    private final ConcurrentMap<Class<? extends Annotation>, ModifierContainer> modifiers = new ConcurrentHashMap<>();

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Nullable
    public Object executeModifiers(@Nonnull CommandExecution execution, @Nonnull CommandParameter param, @Nullable Object parsedArgument) throws CommandExitMessage {
        Preconditions.checkNotNull(execution, "CommandExecution cannot be null");
        Preconditions.checkNotNull(param, "CommandParameter cannot be null");
        for (Annotation annotation : param.getModifierAnnotations()) {
            ModifierContainer container = getModifiers(annotation.annotationType());
            if (container != null) {
                for (ICommandModifier modifier : Objects.requireNonNull(container.getModifiersFor(param.getType()))) {
                    parsedArgument = modifier.modify(execution, param, parsedArgument);
                }
            }
        }
        return parsedArgument;
    }

    public <T> void registerModifier(@Nonnull Class<? extends Annotation> annotation, @Nonnull Class<T> type, @Nonnull ICommandModifier<T> modifier) {
        Preconditions.checkNotNull(annotation, "Annotation cannot be null");
        Preconditions.checkNotNull(type, "Type cannot be null");
        Preconditions.checkNotNull(modifier, "Modifier cannot be null");
        modifiers.computeIfAbsent(annotation, a -> new ModifierContainer()).getModifiers().computeIfAbsent(type, t -> new HashSet<>()).add(modifier);
    }

    @Nullable
    public ModifierContainer getModifiers(@Nonnull Class<? extends Annotation> annotation) {
        Preconditions.checkNotNull(annotation, "Annotation cannot be null");
        Preconditions.checkState(isModifier(annotation), "Annotation provided is not a modifier (annotate with @Modifier) for getModifier: " + annotation.getSimpleName());
        Preconditions.checkState(!isClassifier(annotation), "Annotation provided cannot be an @Classifier and an @Modifier: " + annotation.getSimpleName());
        if (modifiers.containsKey(annotation)) {
            return modifiers.get(annotation);
        }
        return null;
    }

    public boolean isModifier(@Nonnull Class<? extends Annotation> type) {
        return type.isAnnotationPresent(Modifier.class);
    }

    public boolean isClassifier(@Nonnull Class<? extends Annotation> type) {
        return type.isAnnotationPresent(Classifier.class);
    }
}
