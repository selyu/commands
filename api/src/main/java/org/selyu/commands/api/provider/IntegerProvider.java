package org.selyu.commands.api.provider;

import org.selyu.commands.api.argument.CommandArg;
import java.lang.IllegalArgumentException;
import org.selyu.commands.api.lang.Lang;
import org.selyu.commands.api.parametric.ICommandProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class IntegerProvider implements ICommandProvider<Integer> {
    private final Lang lang;

    public IntegerProvider(@Nonnull Lang lang) {
        this.lang = lang;
    }

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean allowNullArgument() {
        return false;
    }

    @Nullable
    @Override
    public Integer defaultNullValue() {
        return 0;
    }

    @Override
    @Nullable
    public Integer provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws IllegalArgumentException {
        String s = arg.get();
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(lang.get(Lang.Type.INVALID_INTEGER, s));
        }
    }

    @Nonnull
    @Override
    public String argumentDescription() {
        return "integer";
    }

    @Nonnull
    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }
}
