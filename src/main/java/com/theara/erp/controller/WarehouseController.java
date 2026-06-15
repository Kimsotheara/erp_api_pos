package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.WarehouseRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/warehouses")
@Tag(name = "Warehouse", description = "Inventory — warehouse management")
public class WarehouseController {

    private final WarehouseService service;

    @Operation(summary = "List warehouses")
    @GetMapping
    public ResponseEntity<?> list(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(service.getAll(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get warehouse by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return DefaultResponse.withCode(service.getById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create warehouse")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody WarehouseRequest request) {
        return DefaultResponse.withCode(service.create(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update warehouse")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody WarehouseRequest request) {
        return DefaultResponse.withCode(service.update(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete warehouse (soft)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
