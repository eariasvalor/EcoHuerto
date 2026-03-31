package com.huerto.api.application.usecase.variety;

import java.util.UUID;

public interface DeleteVarietyUseCase {
    void execute(UUID id);
}