package org.selyu.commands.api.provider;

import org.selyu.commands.api.argument.CommandArg;
import java.lang.IllegalArgumentException;
import org.selyu.commands.api.lang.Lang;
import org.selyu.commands.api.parametric.ICommandProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class DateProvider implements ICommandProvider<Date> {
    public static final String FORMAT_STR = "yyyy-MM-dd@HH:mm";
    public static final DateFormat FORMAT = new SimpleDateFormat(FORMAT_STR, Locale.ENGLISH);

    private final Lang lang;

    public DateProvider(@Nonnull Lang lang) {
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
    public Date provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws IllegalArgumentException {
        String s = arg.get();
        try {
            return FORMAT.parse(s);
        } catch (ParseException e) {
            throw new IllegalArgumentException(lang.get(Lang.Type.INVALID_DATE, FORMAT_STR));
        }
    }

    @Nonnull
    @Override
    public String argumentDescription() {
        return "date: " + FORMAT_STR;
    }

    @Nonnull
    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        Calendar calendar = Calendar.getInstance();
        return Collections.singletonList(String.format("%d-%02d-%02d@%02d:%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
    }
}
