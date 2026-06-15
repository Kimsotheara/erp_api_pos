package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.SupplierRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/suppliers")
@Tag(name = "Supplier", description = "Supplier management and contacts")
@Slf4j
public class SupplierController {

    private final SupplierService supplierService;

    @Operation(summary = "List suppliers")
    @GetMapping
    public ResponseEntity<?> getSuppliers(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(supplierService.getSuppliers(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get supplier by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getSupplierById(@PathVariable Long id) {
        return DefaultResponse.withCode(supplierService.getSupplierById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create supplier")
    @PostMapping
    public ResponseEntity<?> createSupplier(@Valid @RequestBody SupplierRequest request) {
        return DefaultResponse.withCode(supplierService.createSupplier(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update supplier")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
        return DefaultResponse.withCode(supplierService.updateSupplier(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete supplier", description = "Soft-deletes a supplier.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
