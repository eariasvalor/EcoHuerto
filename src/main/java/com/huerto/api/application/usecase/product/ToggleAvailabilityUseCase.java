package com.huerto.api.application.usecase.product;

import com.huerto.api.domain.model.Product;
import java.util.UUID;

public interface ToggleAvailabilityUseCase {
    Product execute(UUID id);
}