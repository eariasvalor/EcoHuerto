package com.huerto.api.application.impl.variety;

import com.huerto.api.application.usecase.variety.ListVarietiesUseCase;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.ports.out.VarietyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListVarietiesUseCaseImpl implements ListVarietiesUseCase {

    private final VarietyRepository varietyRepository;

    public ListVarietiesUseCaseImpl(VarietyRepository varietyRepository) {
        this.varietyRepository = varietyRepository;
    }

    @Override
    public Page<Variety> execute(Pageable pageable) {
        return varietyRepository.findAll(pageable);
    }
}