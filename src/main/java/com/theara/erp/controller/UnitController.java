package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.UnitRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/units")
@Tag(name = "Unit", description = "Unit of measure management")
@Slf4j
public class UnitController {
    private final UnitService unitService;

    @Operation(summary = "List units")
    @GetMapping
    public ResponseEntity<?> getUnits(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(unitService.getUnits(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get unit by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUnitById(@PathVariable Long id) {
        return DefaultResponse.withCode(unitService.getUnitById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create unit")
    @PostMapping
    public ResponseEntity<?> createUnit(@Valid @RequestBody UnitRequest request) {
        return DefaultResponse.withCode(unitService.createUnit(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update unit")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUnit(@PathVariable Long id, @Valid @RequestBody UnitRequest request) {
        return DefaultResponse.withCode(unitService.updateUnit(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete unit", description = "Soft-deletes a unit.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUnit(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
