package com.huerto.api.infrastructure;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huerto.api.application.usecase.product.CreateProductUseCase;
import com.huerto.api.application.usecase.product.ListProductsUseCase;
import com.huerto.api.application.usecase.product.FindProductUseCase;
import com.huerto.api.application.usecase.product.UpdateProductUseCase;
import com.huerto.api.application.usecase.product.UpdateStockUseCase;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.exception.InsufficientStockException;
import com.huerto.api.domain.exception.ResourceNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    @MockBean ListProductsUseCase listProductsUseCase;
    @MockBean FindProductUseCase findProductUseCase;
    @MockBean UpdateProductUseCase updateProductUseCase;
    @MockBean UpdateStockUseCase updateStockUseCase;

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

    @Test
    void should_return_200_when_product_exists() throws Exception {
        UUID id = UUID.randomUUID();
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato");
        Product product = new Product(
                id, "Tomato", variety,
                Price.of("2.50"), Unit.KG, 100, true, 0
        );

        when(findProductUseCase.execute(id)).thenReturn(product);

        mockMvc.perform(get("/api/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Tomato"))
                .andExpect(jsonPath("$.stock").value(100));
    }

    @Test
    void should_return_404_when_product_does_not_exist() throws Exception {
        UUID id = UUID.randomUUID();

        when(findProductUseCase.execute(id))
                .thenThrow(new ResourceNotFoundException("Product", id));

        mockMvc.perform(get("/api/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_200_when_product_is_updated() throws Exception {
        UUID id = UUID.randomUUID();
        UUID varietyId = UUID.randomUUID();
        Variety variety = new Variety(varietyId, "Raf", "Tomato");

        ProductRequest request = new ProductRequest(
                "Updated Tomato", varietyId, new BigDecimal("3.00"), Unit.KG, 80
        );

        Product updated = new Product(
                id, "Updated Tomato", variety,
                Price.of("3.00"), Unit.KG, 80, true, 1
        );

        when(updateProductUseCase.execute(any())).thenReturn(updated);

        mockMvc.perform(put("/api/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Updated Tomato"))
                .andExpect(jsonPath("$.price").value(3.00));
    }

    @Test
    void should_return_404_when_updating_nonexistent_product() throws Exception {
        UUID id = UUID.randomUUID();

        ProductRequest request = new ProductRequest(
                "Updated Tomato", UUID.randomUUID(), new BigDecimal("3.00"), Unit.KG, 80
        );

        when(updateProductUseCase.execute(any()))
                .thenThrow(new ResourceNotFoundException("Product", id));

        mockMvc.perform(put("/api/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_200_when_stock_is_updated() throws Exception {
        UUID id = UUID.randomUUID();
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato");
        Product updated = new Product(
                id, "Tomato", variety,
                Price.of("2.50"), Unit.KG, 150, true, 1
        );

        when(updateStockUseCase.execute(any())).thenReturn(updated);

        mockMvc.perform(patch("/api/v1/products/{id}/stock", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\": 50}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(150));
    }

    @Test
    void should_return_404_when_product_not_found_on_stock_update() throws Exception {
        UUID id = UUID.randomUUID();

        when(updateStockUseCase.execute(any()))
                .thenThrow(new ResourceNotFoundException("Product", id));

        mockMvc.perform(patch("/api/v1/products/{id}/stock", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\": 50}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_409_when_stock_goes_negative() throws Exception {
        UUID id = UUID.randomUUID();

        when(updateStockUseCase.execute(any()))
                .thenThrow(new InsufficientStockException(id, 10, 50));

        mockMvc.perform(patch("/api/v1/products/{id}/stock", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\": -50}"))
                .andExpect(status().isConflict());
    }
}