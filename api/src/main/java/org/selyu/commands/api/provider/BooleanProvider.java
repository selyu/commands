package org.selyu.commands.api.provider;

import org.selyu.commands.api.argument.CommandArg;
import org.selyu.commands.api.lang.Lang;
import org.selyu.commands.api.parametric.ICommandProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class BooleanProvider implements ICommandProvider<Boolean> {
    private static final List<String> SUGGEST = Collections.unmodifiableList(Arrays.asList("true", "false"));
    private static final List<String> SUGGEST_TRUE = Collections.singletonList("true");
    private static final List<String> SUGGEST_FALSE = Collections.singletonList("false");
    private final Lang lang;

    public BooleanProvider(@Nonnull Lang lang) {
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

    @Nullable
    @Override
    public Boolean defaultNullValue() {
        return false;
    }

    @Override
    public Boolean provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws IllegalArgumentException {
        String s = arg.get();
        if (s == null) {
            return false;
        }
        try {
            return Boolean.parseBoolean(s);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(lang.get("invalid_boolean", s));
        }
    }

    @Nonnull
    @Override
    public String argumentDescription() {
        return "true/false";
    }

    @Nonnull
    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        prefix = prefix.toLowerCase();
        if (prefix.length() == 0) {
            return SUGGEST;
        }
        if ("true".startsWith(prefix)) {
            return SUGGEST_TRUE;
        } else if ("false".startsWith(prefix)) {
            return SUGGEST_FALSE;
        } else {
            return Collections.emptyList();
        }
    }
}
