package com.huerto.api.application.usecase.variety;

import com.huerto.api.domain.model.Variety;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListVarietiesUseCase {
    Page<Variety> execute(Pageable pageable);
}