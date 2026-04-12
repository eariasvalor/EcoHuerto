package com.huerto.api.application.usecase.variety;

import com.huerto.api.domain.model.Variety;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UploadVarietyImageUseCase {
    Variety execute(UUID varietyId, MultipartFile file);
}