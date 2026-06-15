package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.BranchTransferRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.BranchTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/branch-transfers")
@Tag(name = "Branch Transfer", description = "Inter-branch stock transfers (ship / receive)")
@Slf4j
public class BranchTransferController {
    private final BranchTransferService branchTransferService;

    @Operation(summary = "List branch transfers")
    @GetMapping
    public ResponseEntity<?> getTransfers(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(branchTransferService.getTransfers(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get branch transfer by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransferById(@PathVariable Long id) {
        return DefaultResponse.withCode(branchTransferService.getTransferById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create branch transfer", description = "Creates a DRAFT transfer.")
    @PostMapping
    public ResponseEntity<?> createTransfer(@Valid @RequestBody BranchTransferRequest request) {
        return DefaultResponse.withCode(branchTransferService.createTransfer(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Ship transfer", description = "DRAFT -> IN_TRANSIT; deducts source-warehouse stock.")
    @PostMapping("/{id}/ship")
    public ResponseEntity<?> shipTransfer(@PathVariable Long id) {
        return DefaultResponse.withCode(branchTransferService.shipTransfer(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Receive transfer", description = "IN_TRANSIT -> RECEIVED; adds destination-warehouse stock.")
    @PostMapping("/{id}/receive")
    public ResponseEntity<?> receiveTransfer(@PathVariable Long id) {
        return DefaultResponse.withCode(branchTransferService.receiveTransfer(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Cancel transfer", description = "Restores in-transit stock to the source warehouse.")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelTransfer(@PathVariable Long id) {
        return DefaultResponse.withCode(branchTransferService.cancelTransfer(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete branch transfer", description = "Soft-deletes a DRAFT or CANCELLED transfer.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransfer(@PathVariable Long id) {
        branchTransferService.deleteTransfer(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
