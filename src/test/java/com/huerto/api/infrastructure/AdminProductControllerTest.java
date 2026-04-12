package com.huerto.api.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huerto.api.application.usecase.product.ListAllProductsUseCase;
import com.huerto.api.application.usecase.product.UploadProductImageUseCase;
import com.huerto.api.application.usecase.product.DeleteProductImageUseCase;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.exception.ResourceNotFoundException;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    @MockBean
    UploadProductImageUseCase uploadProductImageUseCase;
    @MockBean DeleteProductImageUseCase deleteProductImageUseCase;

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

    @Test
    void should_return_200_when_image_is_uploaded() throws Exception {
        UUID productId = UUID.randomUUID();
        Product updated = new Product(
                productId, "Tomato",
                new Variety(UUID.randomUUID(), "Raf", "Tomato", null),
                Price.of("2.50"), Unit.KG, 100, true,
                "https://res.cloudinary.com/huerto/image/upload/huerto/categories/abc.jpg",
                0
        );

        MockMultipartFile file = new MockMultipartFile(
                "file", "tomato.jpg", MediaType.IMAGE_JPEG_VALUE, "image-bytes".getBytes()
        );

        when(uploadProductImageUseCase.execute(eq(productId), any())).thenReturn(updated);

        mockMvc.perform(multipart("/api/v1/admin/products/{id}/image", productId)
                        .file(file)
                        .with(req -> { req.setMethod("PATCH"); return req; }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").value(
                        "https://res.cloudinary.com/huerto/image/upload/huerto/categories/abc.jpg"));
    }

    @Test
    void should_return_404_when_product_not_found_on_image_upload() throws Exception {
        UUID productId = UUID.randomUUID();

        MockMultipartFile file = new MockMultipartFile(
                "file", "tomato.jpg", MediaType.IMAGE_JPEG_VALUE, "image-bytes".getBytes()
        );

        when(uploadProductImageUseCase.execute(eq(productId), any()))
                .thenThrow(new ResourceNotFoundException("Product", productId));

        mockMvc.perform(multipart("/api/v1/admin/products/{id}/image", productId)
                        .file(file)
                        .with(req -> { req.setMethod("PATCH"); return req; }))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_400_when_file_is_empty() throws Exception {
        UUID productId = UUID.randomUUID();

        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "tomato.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[0]
        );

        mockMvc.perform(multipart("/api/v1/admin/products/{id}/image", productId)
                        .file(emptyFile)
                        .with(req -> { req.setMethod("PATCH"); return req; }))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_204_when_image_is_deleted() throws Exception {
        UUID productId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/admin/products/{id}/image", productId))
                .andExpect(status().isNoContent());

        verify(deleteProductImageUseCase).execute(productId);
    }

    @Test
    void should_return_404_when_product_not_found_on_image_delete() throws Exception {
        UUID productId = UUID.randomUUID();

        doThrow(new ResourceNotFoundException("Product", productId))
                .when(deleteProductImageUseCase).execute(productId);

        mockMvc.perform(delete("/api/v1/admin/products/{id}/image", productId))
                .andExpect(status().isNotFound());
    }
}