package com.huerto.api.infrastructure;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huerto.api.application.usecase.product.CreateProductUseCase;
import com.huerto.api.application.usecase.product.ListProductsUseCase;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.valueobject.Price;
import com.huerto.api.infrastructure.adapters.in.web.ProductController;
import com.huerto.api.infrastructure.adapters.in.web.dto.ProductRequest;
import com.huerto.api.infrastructure.config.SecurityConfig;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = ProductController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class
        )
)
class ProductControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean CreateProductUseCase createProductUseCase;
    @MockBean
    ListProductsUseCase listProductsUseCase;

    @Test
    void should_return_201_when_product_is_created() throws Exception {
        UUID varietyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        ProductRequest request = new ProductRequest(
                "Tomato",
                varietyId,
                new BigDecimal("2.50"),
                Unit.KG,
                100
        );

        Variety variety = new Variety(varietyId, "Raf", "Tomato");
        Product created = new Product(
                productId, "Tomato", variety,
                Price.of("2.50"), Unit.KG, 100, true, 0
        );

        when(createProductUseCase.execute(any())).thenReturn(created);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.name").value("Tomato"))
                .andExpect(jsonPath("$.price").value(2.50))
                .andExpect(jsonPath("$.unit").value("KG"))
                .andExpect(jsonPath("$.stock").value(100))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void should_return_400_when_name_is_blank() throws Exception {
        ProductRequest request = new ProductRequest(
                "",
                UUID.randomUUID(),
                new BigDecimal("2.50"),
                Unit.KG,
                100
        );

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_400_when_price_is_negative() throws Exception {
        ProductRequest request = new ProductRequest(
                "Tomato",
                UUID.randomUUID(),
                new BigDecimal("-1.00"),
                Unit.KG,
                100
        );

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_200_with_paginated_products() throws Exception {
        UUID varietyId = UUID.randomUUID();
        Variety variety = new Variety(varietyId, "Raf", "Tomato");

        Product product1 = new Product(
                UUID.randomUUID(), "Tomato", variety,
                Price.of("2.50"), Unit.KG, 100, true, 0
        );
        Product product2 = new Product(
                UUID.randomUUID(), "Cherry Tomato", variety,
                Price.of("3.00"), Unit.KG, 50, true, 0
        );

        Page<Product> page = new PageImpl<>(
                List.of(product1, product2),
                PageRequest.of(0, 10),
                2
        );

        when(listProductsUseCase.execute(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/products")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Tomato"));
    }

    @Test
    void should_return_200_with_empty_list_when_no_products() throws Exception {
        when(listProductsUseCase.execute(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/products")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }
}