package com.huerto.api.application.usecase.auth;

import com.huerto.api.application.commands.LoginCommand;

public interface LoginCustomerUseCase {
    String execute(LoginCommand command);
}