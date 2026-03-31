package com.huerto.api.application.usecase.auth;

import com.huerto.api.application.commands.LoginCommand;

public interface LoginAdminUseCase {
    String execute(LoginCommand command);
}