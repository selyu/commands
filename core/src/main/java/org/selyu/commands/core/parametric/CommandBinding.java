package org.selyu.commands.core.parametric;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.provider.IParameterProvider;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;

@Getter
public class CommandBinding<T> {
    private final Class<T> type;
    private final Set<Class<? extends Annotation>> annotations;
    private final IParameterProvider<T> provider;

    public CommandBinding(Class<T> type, Set<Class<? extends Annotation>> annotations, IParameterProvider<T> provider) {
        this.type = type;
        this.annotations = annotations;
        this.provider = provider;
    }

    public boolean canProvideFor(@NotNull CommandParameter parameter) {
        Objects.requireNonNull(parameter, "Parameter cannot be null");
        // The parameter and binding need to have exact same annotations
        for (Annotation c : parameter.getClassifierAnnotations()) {
            if (!annotations.contains(c.annotationType())) {
                return false;
            }
        }
        for (Class<? extends Annotation> annotation : annotations) {
            if (parameter.getClassifierAnnotations().stream().noneMatch(a -> a.annotationType().equals(annotation))) {
                return false;
            }
        }
        return true;
    }
}
