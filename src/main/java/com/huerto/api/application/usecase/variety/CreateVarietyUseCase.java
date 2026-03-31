package com.huerto.api.application.usecase.variety;

import com.huerto.api.application.commands.CreateVarietyCommand;
import com.huerto.api.domain.model.Variety;

public interface CreateVarietyUseCase {
    Variety execute(CreateVarietyCommand command);
}