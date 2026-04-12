package com.huerto.api.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huerto.api.application.usecase.product.ListAllProductsUseCase;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.valueobject.Price;
import com.huerto.api.infrastructure.adapters.in.web.AdminProductController;
import com.huerto.api.infrastructure.config.SecurityConfig;
import com.huerto.api.infrastructure.config.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = AdminProductController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class
        )
)
class AdminProductControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean ListAllProductsUseCase listAllProductsUseCase;
    @MockBean SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        when(securityContext.getCurrentUserId()).thenReturn(UUID.randomUUID());
        when(securityContext.isAdmin()).thenReturn(true);
    }

    @Test
    void should_return_200_with_all_products_including_unavailable() throws Exception {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);
        Product available = new Product(
                UUID.randomUUID(), "Tomato", variety,
                Price.of("2.50"), Unit.KG, 100, true, null,0
        );
        Product unavailable = new Product(
                UUID.randomUUID(), "Cherry Tomato", variety,
                Price.of("3.00"), Unit.KG, 0, false, null,0
        );

        Page<Product> page = new PageImpl<>(
                List.of(available, unavailable), PageRequest.of(0, 10), 2
        );

        when(listAllProductsUseCase.execute(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/products")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].available").value(true))
                .andExpect(jsonPath("$.content[1].available").value(false));
    }

    @Test
    void should_return_200_with_empty_page_when_no_products() throws Exception {
        when(listAllProductsUseCase.execute(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/admin/products")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }
}