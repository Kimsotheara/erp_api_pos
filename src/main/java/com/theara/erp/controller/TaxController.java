package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.TaxRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.TaxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/taxes")
@Tag(name = "Tax", description = "Tax rate management")
@Slf4j
public class TaxController {

    private final TaxService taxService;

    @Operation(summary = "List taxes")
    @GetMapping
    public ResponseEntity<?> getTaxes(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(taxService.getTaxes(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get tax by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaxById(@PathVariable Long id) {
        return DefaultResponse.withCode(taxService.getTaxById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create tax")
    @PostMapping
    public ResponseEntity<?> createTax(@Valid @RequestBody TaxRequest request) {
        return DefaultResponse.withCode(taxService.createTax(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update tax")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTax(@PathVariable Long id, @Valid @RequestBody TaxRequest request) {
        return DefaultResponse.withCode(taxService.updateTax(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete tax", description = "Soft-deletes a tax.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTax(@PathVariable Long id) {
        taxService.deleteTax(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
