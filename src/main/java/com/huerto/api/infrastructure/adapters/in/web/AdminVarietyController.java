package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.usecase.variety.UploadVarietyImageUseCase;
import com.huerto.api.infrastructure.adapters.in.web.dto.VarietyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/varieties")
@Tag(name = "Admin — Varieties", description = "Variety management for administrators")
public class AdminVarietyController {

    private final UploadVarietyImageUseCase uploadVarietyImageUseCase;

    public AdminVarietyController(UploadVarietyImageUseCase uploadVarietyImageUseCase) {
        this.uploadVarietyImageUseCase = uploadVarietyImageUseCase;
    }

    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload or replace the image for a variety")
    @ApiResponse(responseCode = "200", description = "Image uploaded")
    @ApiResponse(responseCode = "400", description = "File is empty")
    @ApiResponse(responseCode = "404", description = "Variety not found")
    public VarietyResponse uploadImage(@PathVariable UUID id,
                                       @RequestParam("file") MultipartFile file) {
        if (file.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File must not be empty");

        return VarietyResponse.from(uploadVarietyImageUseCase.execute(id, file));
    }
}