package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.BranchRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.BranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/branches")
@Tag(name = "Branch", description = "Branch / outlet management")
@Slf4j
public class BranchController {

    private final BranchService branchService;

    @Operation(summary = "List branches")
    @GetMapping
    public ResponseEntity<?> getBranches(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(branchService.getBranches(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get branch by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getBranchById(@PathVariable Long id) {
        return DefaultResponse.withCode(branchService.getBranchById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create branch")
    @PostMapping
    public ResponseEntity<?> createBranch(@Valid @RequestBody BranchRequest request) {
        return DefaultResponse.withCode(branchService.createBranch(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update branch")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBranch(@PathVariable Long id, @Valid @RequestBody BranchRequest request) {
        return DefaultResponse.withCode(branchService.updateBranch(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete branch", description = "Soft-deletes a branch.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
