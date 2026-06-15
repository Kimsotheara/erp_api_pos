package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.AttendancePunchRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.StaffShiftRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.StaffShiftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/staff-shifts")
@Tag(name = "Staff Shift", description = "Staff: roster assignment and clock-in/out attendance")
@Slf4j
public class StaffShiftController {
    private final StaffShiftService staffShiftService;

    @Operation(summary = "List staff shifts")
    @GetMapping
    public ResponseEntity<?> getStaffShifts(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(staffShiftService.getStaffShifts(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get staff shift by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getStaffShiftById(@PathVariable Long id) {
        return DefaultResponse.withCode(staffShiftService.getStaffShiftById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Roster a staff shift", description = "Assigns a user to a shift on a date (status SCHEDULED).")
    @PostMapping
    public ResponseEntity<?> rosterStaffShift(@Valid @RequestBody StaffShiftRequest request) {
        return DefaultResponse.withCode(staffShiftService.rosterStaffShift(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Record an attendance punch",
            description = "CLOCK_IN opens the session; CLOCK_OUT closes it and computes worked minutes.")
    @PostMapping("/{id}/punch")
    public ResponseEntity<?> punch(@PathVariable Long id, @Valid @RequestBody AttendancePunchRequest request) {
        return DefaultResponse.withCode(staffShiftService.punch(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Cancel a staff shift")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelStaffShift(@PathVariable Long id) {
        return DefaultResponse.withCode(staffShiftService.cancelStaffShift(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete staff shift", description = "Soft-deletes a staff shift.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaffShift(@PathVariable Long id) {
        staffShiftService.deleteStaffShift(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
