package org.selyu.commands.api.parametric;

import lombok.Getter;
import org.selyu.commands.api.util.CommandUtil;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Set;

@Getter
public class CommandBinding<T> {
    private final Class<T> type;
    private final Set<Class<? extends Annotation>> annotations;
    private final CommandProvider<T> provider;

    public CommandBinding(Class<T> type, Set<Class<? extends Annotation>> annotations, CommandProvider<T> provider) {
        this.type = type;
        this.annotations = annotations;
        this.provider = provider;
    }

    public boolean canProvideFor(@Nonnull CommandParameter parameter) {
        CommandUtil.checkNotNull(parameter, "Parameter cannot be null");
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
