package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.commands.CreateVarietyCommand;
import com.huerto.api.application.usecase.variety.CreateVarietyUseCase;
import com.huerto.api.application.usecase.variety.ListVarietiesUseCase;
import com.huerto.api.infrastructure.adapters.in.web.dto.VarietyRequest;
import com.huerto.api.infrastructure.adapters.in.web.dto.VarietyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/varieties")
@Tag(name = "Varieties", description = "Product variety management")
public class VarietyController {

    private final CreateVarietyUseCase createVarietyUseCase;
    private final ListVarietiesUseCase listVarietiesUseCase;

    public VarietyController(CreateVarietyUseCase createVarietyUseCase,
                             ListVarietiesUseCase listVarietiesUseCase) {
        this.createVarietyUseCase = createVarietyUseCase;
        this.listVarietiesUseCase = listVarietiesUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new variety")
            @ApiResponse(responseCode = "201", description = "Variety created")
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    public VarietyResponse create(@Valid @RequestBody VarietyRequest request) {
        CreateVarietyCommand command = new CreateVarietyCommand(
                request.name(),
                request.productCategory()
        );
        return VarietyResponse.from(createVarietyUseCase.execute(command));
    }

    @GetMapping
    @Operation(summary = "List all varieties")
    @ApiResponse(responseCode = "200", description = "Paginated variety list")
    public Page<VarietyResponse> list(Pageable pageable) {
        return listVarietiesUseCase.execute(pageable).map(VarietyResponse::from);
    }
}