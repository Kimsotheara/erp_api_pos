package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.BrandRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brands")
@Tag(name = "Brand", description = "Product brand management")
@Slf4j
public class BrandController {
    private final BrandService brandService;

    @Operation(summary = "List brands")
    @GetMapping
    public ResponseEntity<?> getBrands(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(brandService.getBrands(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get brand by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getBrandById(@PathVariable Long id) {
        return DefaultResponse.withCode(brandService.getBrandById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create brand")
    @PostMapping
    public ResponseEntity<?> createBrand(@Valid @RequestBody BrandRequest request) {
        return DefaultResponse.withCode(brandService.createBrand(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update brand")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBrand(@PathVariable Long id, @Valid @RequestBody BrandRequest request) {
        return DefaultResponse.withCode(brandService.updateBrand(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete brand", description = "Soft-deletes a brand.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
