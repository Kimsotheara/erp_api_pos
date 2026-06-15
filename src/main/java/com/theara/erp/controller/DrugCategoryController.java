package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.DrugCategoryRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.DrugCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drug-categories")
@Tag(name = "Drug Category", description = "Pharmacy: drug classifications")
@Slf4j
public class DrugCategoryController {

    private final DrugCategoryService drugCategoryService;

    @Operation(summary = "List drug categories")
    @GetMapping
    public ResponseEntity<?> getDrugCategories(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(drugCategoryService.getDrugCategories(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get drug category by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getDrugCategoryById(@PathVariable Long id) {
        return DefaultResponse.withCode(drugCategoryService.getDrugCategoryById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create drug category")
    @PostMapping
    public ResponseEntity<?> createDrugCategory(@Valid @RequestBody DrugCategoryRequest request) {
        return DefaultResponse.withCode(drugCategoryService.createDrugCategory(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update drug category")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDrugCategory(@PathVariable Long id, @Valid @RequestBody DrugCategoryRequest request) {
        return DefaultResponse.withCode(drugCategoryService.updateDrugCategory(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete drug category", description = "Soft-deletes a drug category.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDrugCategory(@PathVariable Long id) {
        drugCategoryService.deleteDrugCategory(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
