package com.huerto.api.application.impl.variety;

import com.huerto.api.application.usecase.variety.DeleteVarietyImageUseCase;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.ImageStoragePort;
import com.huerto.api.domain.ports.out.VarietyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeleteVarietyImageUseCaseImpl implements DeleteVarietyImageUseCase {

    private final VarietyRepository varietyRepository;
    private final ImageStoragePort imageStoragePort;

    public DeleteVarietyImageUseCaseImpl(VarietyRepository varietyRepository,
                                         ImageStoragePort imageStoragePort) {
        this.varietyRepository = varietyRepository;
        this.imageStoragePort = imageStoragePort;
    }

    @Override
    public void execute(UUID varietyId) {
        Variety variety = varietyRepository.findById(varietyId)
                .orElseThrow(() -> new ResourceNotFoundException("Variety", varietyId));

        if (variety.imageUrl() != null) {
            imageStoragePort.delete(varietyId.toString());
        }

        varietyRepository.save(variety.withImageUrl(null));
    }
}