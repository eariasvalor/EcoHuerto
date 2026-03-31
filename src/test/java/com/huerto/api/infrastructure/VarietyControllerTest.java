package com.huerto.api.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huerto.api.application.usecase.variety.CreateVarietyUseCase;
import com.huerto.api.application.usecase.variety.ListVarietiesUseCase;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.infrastructure.adapters.in.web.VarietyController;
import com.huerto.api.infrastructure.adapters.in.web.dto.VarietyRequest;
import com.huerto.api.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import org.springframework.data.domain.*;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = VarietyController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class
        )
)
class VarietyControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean CreateVarietyUseCase createVarietyUseCase;
    @MockBean ListVarietiesUseCase listVarietiesUseCase;

    @Test
    void should_return_201_when_variety_is_created() throws Exception {
        UUID id = UUID.randomUUID();
        VarietyRequest request = new VarietyRequest("Raf", "Tomato");
        Variety created = new Variety(id, "Raf", "Tomato");

        when(createVarietyUseCase.execute(any())).thenReturn(created);

        mockMvc.perform(post("/api/v1/varieties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Raf"))
                .andExpect(jsonPath("$.productCategory").value("Tomato"));
    }

    @Test
    void should_return_400_when_name_is_blank() throws Exception {
        VarietyRequest request = new VarietyRequest("", "Tomato");

        mockMvc.perform(post("/api/v1/varieties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_400_when_category_is_blank() throws Exception {
        VarietyRequest request = new VarietyRequest("Raf", "");

        mockMvc.perform(post("/api/v1/varieties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_200_with_paginated_varieties() throws Exception {
        UUID id = UUID.randomUUID();
        Variety variety = new Variety(id, "Raf", "Tomato");

        Page<Variety> page = new PageImpl<>(
                List.of(variety),
                PageRequest.of(0, 10),
                1
        );

        when(listVarietiesUseCase.execute(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/varieties")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Raf"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void should_return_200_with_empty_page_when_no_varieties() throws Exception {
        when(listVarietiesUseCase.execute(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/varieties")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }
}