package org.selyu.commands.api.argument;

import lombok.Getter;
import org.selyu.commands.api.command.AbstractCommandService;
import org.selyu.commands.api.flag.CommandFlag;
import org.selyu.commands.api.sender.ICommandSender;
import org.selyu.commands.api.util.CommandUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public final class CommandArgs {
    private final AbstractCommandService<?> commandService;
    private final ICommandSender<?> sender;
    private final List<String> args;
    private final String label;
    private final Map<Character, CommandFlag> flags;
    private final ReentrantLock lock = new ReentrantLock();
    private int index = 0;

    public CommandArgs(@Nonnull AbstractCommandService<?> commandService, @Nonnull ICommandSender<?> sender, @Nonnull String label, @Nonnull List<String> args,
                       @Nonnull Map<Character, CommandFlag> flags) {
        CommandUtil.checkNotNull(commandService, "CommandService cannot be null");
        CommandUtil.checkNotNull(sender, "CommandSender cannot be null");
        CommandUtil.checkNotNull(label, "Label cannot be null");
        CommandUtil.checkNotNull(args, "Command args cannot be null");
        this.commandService = commandService;
        this.sender = sender;
        this.label = label;
        this.args = new ArrayList<>(args);
        this.flags = flags;
    }

    public boolean hasNext() {
        lock.lock();
        try {
            return args.size() > index;
        } finally {
            lock.unlock();
        }
    }

    public String next() {
        lock.lock();
        try {
            return args.get(index++);
        } finally {
            lock.unlock();
        }
    }
}
