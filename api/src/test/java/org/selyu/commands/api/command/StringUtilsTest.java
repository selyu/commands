package org.selyu.commands.api.command;

import org.junit.Test;
import org.selyu.commands.api.util.StringUtils;

import static org.junit.Assert.assertEquals;

public class StringUtilsTest {
    @Test
    public void apache_output() {
        // OUTPUT: Apache null:help  hello | Apache Nonnull:help goodbye | Apache oneArg:hi
        // Null values just don't get added but it still adds the separator so it doesn't skip
        // Adds separator before the item because no trailing space
        // Probably adds first argument itself then loops through args.len+1 and adds w/ separator
//        String nullJoin = StringUtils.join(new String[]{"help", null, "hello"}, ' ');
//        String nonNullJoin = StringUtils.join(new String[]{"help", "goodbye"}, ' ');
//        String oneArg = StringUtils.join(new String[]{"hi"}, ' ');
//
//        System.out.println("Apache null:" + nullJoin);
//        System.out.println("Apache Nonnull:" + nonNullJoin);
//        System.out.println("Apache oneArg:" + oneArg);
    }

    @Test
    public void our_output() {
        String ourNull = StringUtils.join(new String[]{"help", null, "hello"}, ' ');
        String ourNonNull = StringUtils.join(new String[]{"help", "goodbye"}, ' ');
        String ourOneArg = StringUtils.join(new String[]{"hi"}, ' ');

        assertEquals(ourNull, "help  hello");
        assertEquals(ourNonNull, "help goodbye");
        assertEquals(ourOneArg, "hi");
    }
}
