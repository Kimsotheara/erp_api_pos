package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.PrescriptionRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/prescriptions")
@Tag(name = "Prescription", description = "Pharmacy: prescription records")
@Slf4j
public class PrescriptionController {
    private final PrescriptionService prescriptionService;

    @Operation(summary = "List prescriptions")
    @GetMapping
    public ResponseEntity<?> getPrescriptions(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(prescriptionService.getPrescriptions(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get prescription by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getPrescriptionById(@PathVariable Long id) {
        return DefaultResponse.withCode(prescriptionService.getPrescriptionById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create prescription")
    @PostMapping
    public ResponseEntity<?> createPrescription(@Valid @RequestBody PrescriptionRequest request) {
        return DefaultResponse.withCode(prescriptionService.createPrescription(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update prescription")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePrescription(@PathVariable Long id, @Valid @RequestBody PrescriptionRequest request) {
        return DefaultResponse.withCode(prescriptionService.updatePrescription(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete prescription")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePrescription(@PathVariable Long id) {
        prescriptionService.deletePrescription(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
