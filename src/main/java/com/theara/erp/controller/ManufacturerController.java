package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.ManufacturerRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.ManufacturerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/manufacturers")
@Tag(name = "Manufacturer", description = "Pharmacy: drug manufacturers")
@Slf4j
public class ManufacturerController {
    private final ManufacturerService manufacturerService;

    @Operation(summary = "List manufacturers")
    @GetMapping
    public ResponseEntity<?> getManufacturers(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(manufacturerService.getManufacturers(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get manufacturer by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getManufacturerById(@PathVariable Long id) {
        return DefaultResponse.withCode(manufacturerService.getManufacturerById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create manufacturer")
    @PostMapping
    public ResponseEntity<?> createManufacturer(@Valid @RequestBody ManufacturerRequest request) {
        return DefaultResponse.withCode(manufacturerService.createManufacturer(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update manufacturer")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateManufacturer(@PathVariable Long id, @Valid @RequestBody ManufacturerRequest request) {
        return DefaultResponse.withCode(manufacturerService.updateManufacturer(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete manufacturer", description = "Soft-deletes a manufacturer.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteManufacturer(@PathVariable Long id) {
        manufacturerService.deleteManufacturer(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
