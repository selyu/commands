package org.selyu.commands.api.authorizer.impl;

import org.selyu.commands.api.authorizer.IAuthorizer;
import org.selyu.commands.api.command.WrappedCommand;
import org.selyu.commands.api.lang.Lang;
import org.selyu.commands.api.sender.ICommandSender;

import javax.annotation.Nonnull;

public class Authorizer<T> implements IAuthorizer<T> {
    private final Lang lang;

    public Authorizer(@Nonnull Lang lang) {
        this.lang = lang;
    }

    @Override
    public boolean isAuthorized(@Nonnull ICommandSender<T> sender, @Nonnull WrappedCommand command) {
        if (command.getPermission() != null && command.getPermission().length() > 0) {
            if (!sender.hasPermission(command.getPermission())) {
                sender.sendMessage(lang.get("no_permission"));
                return false;
            }
        }
        return true;
    }
}
