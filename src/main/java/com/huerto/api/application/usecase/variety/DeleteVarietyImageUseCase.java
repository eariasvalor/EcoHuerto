package com.huerto.api.application.usecase.variety;

import java.util.UUID;

public interface DeleteVarietyImageUseCase {
    void execute(UUID varietyId);
}