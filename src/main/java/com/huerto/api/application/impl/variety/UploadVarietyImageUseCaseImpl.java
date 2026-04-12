package com.huerto.api.application.impl.variety;

import com.huerto.api.application.usecase.variety.UploadVarietyImageUseCase;
import com.huerto.api.domain.exception.ImageUploadException;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.ImageStoragePort;
import com.huerto.api.domain.ports.out.VarietyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Transactional
public class UploadVarietyImageUseCaseImpl implements UploadVarietyImageUseCase {

    private static final String FOLDER = "huerto/varieties";

    private final VarietyRepository varietyRepository;
    private final ImageStoragePort imageStoragePort;

    public UploadVarietyImageUseCaseImpl(VarietyRepository varietyRepository,
                                         ImageStoragePort imageStoragePort) {
        this.varietyRepository = varietyRepository;
        this.imageStoragePort = imageStoragePort;
    }

    @Override
    public Variety execute(UUID varietyId, MultipartFile file) {
        Variety variety = varietyRepository.findById(varietyId)
                .orElseThrow(() -> new ResourceNotFoundException("Variety", varietyId));

        if (variety.imageUrl() != null) {
            imageStoragePort.delete(varietyId.toString());
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new ImageUploadException("Failed to read image file", e);
        }

        ImageStoragePort.ImageUploadResult result =
                imageStoragePort.upload(bytes, file.getOriginalFilename(), FOLDER);

        return varietyRepository.save(variety.withImageUrl(result.secureUrl()));
    }
}