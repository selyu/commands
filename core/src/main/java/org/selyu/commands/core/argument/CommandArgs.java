package org.selyu.commands.core.argument;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.selyu.commands.core.command.AbstractCommandService;
import org.selyu.commands.core.executor.ICommandExecutor;
import org.selyu.commands.core.flag.CommandFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public final class CommandArgs {
    private final AbstractCommandService<?> commandService;
    private final ICommandExecutor<?> sender;
    private final List<String> args;
    private final String label;
    private final Map<Character, CommandFlag> flags;
    private final ReentrantLock lock = new ReentrantLock();
    private int index = 0;

    public CommandArgs(@NotNull AbstractCommandService<?> commandService, @NotNull ICommandExecutor<?> sender, @NotNull String label, @NotNull List<String> args,
                       @NotNull Map<Character, CommandFlag> flags) {
        Objects.requireNonNull(commandService, "CommandService cannot be null");
        Objects.requireNonNull(sender, "CommandSender cannot be null");
        Objects.requireNonNull(label, "Label cannot be null");
        Objects.requireNonNull(args, "Command args cannot be null");
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
