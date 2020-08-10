package org.selyu.commands.api.authorizer.impl;

import org.selyu.commands.api.authorizer.IAuthorizer;
import org.selyu.commands.api.command.WrappedCommand;
import org.selyu.commands.api.lang.Lang;
import org.selyu.commands.api.sender.ICommandSender;

import javax.annotation.Nonnull;

public class AuthorizerImpl<T> implements IAuthorizer<T> {
    private final Lang lang;

    public AuthorizerImpl(@Nonnull Lang lang) {
        this.lang = lang;
    }

    @Override
    public boolean isAuthorized(@Nonnull ICommandSender<T> sender, @Nonnull WrappedCommand command) {
        if (command.getPermission() != null && command.getPermission().length() > 0) {
            if (!sender.hasPermission(command.getPermission())) {
                sender.sendMessage(lang.get(Lang.Type.NO_PERMISSION));
                return false;
            }
        }
        return true;
    }
}
