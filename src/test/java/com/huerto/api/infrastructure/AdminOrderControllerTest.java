package com.huerto.api.infrastructure;

import com.huerto.api.application.usecase.order.GetOrderStatsUseCase;
import com.huerto.api.domain.model.OrderStats;
import com.huerto.api.infrastructure.adapters.in.web.AdminOrderController;
import com.huerto.api.infrastructure.config.SecurityConfig;
import com.huerto.api.infrastructure.config.SecurityContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = AdminOrderController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class
        )
)
class AdminOrderControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean GetOrderStatsUseCase getOrderStatsUseCase;
    @MockBean SecurityContext securityContext;

    @Test
    void should_return_200_with_order_stats() throws Exception {
        OrderStats stats = new OrderStats(3, 5, 2, 1, 8, 19);

        when(getOrderStatsUseCase.execute()).thenReturn(stats);

        mockMvc.perform(get("/api/v1/admin/orders/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pending").value(3))
                .andExpect(jsonPath("$.confirmed").value(5))
                .andExpect(jsonPath("$.delivered").value(1))
                .andExpect(jsonPath("$.ready").value(2))
                .andExpect(jsonPath("$.cancelled").value(8))
                .andExpect(jsonPath("$.total").value(19));
    }
}