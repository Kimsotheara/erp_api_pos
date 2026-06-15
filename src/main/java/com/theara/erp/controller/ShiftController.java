package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.ShiftRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.ShiftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shifts")
@Tag(name = "Shift", description = "Staff: reusable shift templates per branch")
@Slf4j
public class ShiftController {
    private final ShiftService shiftService;

    @Operation(summary = "List shifts")
    @GetMapping
    public ResponseEntity<?> getShifts(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(shiftService.getShifts(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get shift by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getShiftById(@PathVariable Long id) {
        return DefaultResponse.withCode(shiftService.getShiftById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create shift template")
    @PostMapping
    public ResponseEntity<?> createShift(@Valid @RequestBody ShiftRequest request) {
        return DefaultResponse.withCode(shiftService.createShift(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update shift template")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateShift(@PathVariable Long id, @Valid @RequestBody ShiftRequest request) {
        return DefaultResponse.withCode(shiftService.updateShift(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete shift template", description = "Soft-deletes a shift template.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShift(@PathVariable Long id) {
        shiftService.deleteShift(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
