package com.huerto.api.application.impl.variety;

import com.huerto.api.application.usecase.variety.DeleteVarietyUseCase;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.exception.VarietyInUseException;
import com.huerto.api.domain.ports.out.ProductRepository;
import com.huerto.api.domain.ports.out.VarietyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeleteVarietyUseCaseImpl implements DeleteVarietyUseCase {

    private final VarietyRepository varietyRepository;
    private final ProductRepository productRepository;

    public DeleteVarietyUseCaseImpl(VarietyRepository varietyRepository,
                                    ProductRepository productRepository) {
        this.varietyRepository = varietyRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void execute(UUID id) {
        if (!varietyRepository.existsById(id))
            throw new ResourceNotFoundException("Variety", id);

        if (productRepository.existsByVarietyId(id))
            throw new VarietyInUseException(id);

        varietyRepository.deleteById(id);
    }
}