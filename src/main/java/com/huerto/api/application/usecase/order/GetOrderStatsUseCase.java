package com.huerto.api.application.usecase.order;

import com.huerto.api.domain.model.OrderStats;

public interface GetOrderStatsUseCase {
    OrderStats execute();
}