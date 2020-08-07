package org.selyu.commands.api.provider;

import org.selyu.commands.api.argument.CommandArg;
import org.selyu.commands.api.argument.CommandArgs;
import org.selyu.commands.api.parametric.CommandProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class CommandArgsProvider extends CommandProvider<CommandArgs> {
    @Override
    public boolean doesConsumeArgument() {
        return false;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Nullable
    @Override
    public CommandArgs provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) {
        return arg.getArgs();
    }

    @Override
    public String argumentDescription() {
        return "args";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }
}
