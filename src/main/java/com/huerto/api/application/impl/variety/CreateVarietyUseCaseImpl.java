package com.huerto.api.application.impl.variety;

import com.fasterxml.uuid.Generators;
import com.huerto.api.application.commands.CreateVarietyCommand;
import com.huerto.api.application.usecase.variety.CreateVarietyUseCase;
import com.huerto.api.domain.exception.DuplicateVarietyException;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.VarietyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateVarietyUseCaseImpl implements CreateVarietyUseCase {

    private final VarietyRepository varietyRepository;

    public CreateVarietyUseCaseImpl(VarietyRepository varietyRepository) {
        this.varietyRepository = varietyRepository;
    }

    @Override
    public Variety execute(CreateVarietyCommand command) {
        boolean exists = varietyRepository.existsByNameAndProductCategory(
                command.name(), command.productCategory());

        if (exists)
            throw new DuplicateVarietyException(command.name(), command.productCategory());

        Variety variety = new Variety(
                Generators.timeBasedEpochGenerator().generate(),
                command.name(),
                command.productCategory(),
                null
        );
        return varietyRepository.save(variety);
    }
}