package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.MedicineBatchRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.MedicineBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/medicine-batches")
@Tag(name = "Medicine Batch", description = "Pharmacy: batch tracking, expiry monitoring, stock-in")
@Slf4j
public class MedicineBatchController {
    private final MedicineBatchService medicineBatchService;

    @Operation(summary = "List medicine batches")
    @GetMapping
    public ResponseEntity<?> getBatches(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(medicineBatchService.getBatches(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "List expiring batches", description = "Batches expiring within the given number of days that still hold stock.")
    @GetMapping("/expiring")
    public ResponseEntity<?> getExpiringBatches(@RequestParam(defaultValue = "30") int withinDays,
                                                PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(medicineBatchService.getExpiringBatches(withinDays, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get medicine batch by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getBatchById(@PathVariable Long id) {
        return DefaultResponse.withCode(medicineBatchService.getBatchById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Receive a medicine batch", description = "Creates a batch and increases inventory (stock-in).")
    @PostMapping
    public ResponseEntity<?> createBatch(@Valid @RequestBody MedicineBatchRequest request) {
        return DefaultResponse.withCode(medicineBatchService.createBatch(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Delete medicine batch")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBatch(@PathVariable Long id) {
        medicineBatchService.deleteBatch(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
