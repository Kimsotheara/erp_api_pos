package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.PermissionRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/permissions")
@Tag(name = "Permission", description = "Fine-grained permission catalog")
@Slf4j
public class PermissionController {
    private final PermissionService permissionService;

    @Operation(summary = "List permissions")
    @GetMapping
    public ResponseEntity<?> getPermissions(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(permissionService.getPermissions(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get permission by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getPermissionById(@PathVariable Long id) {
        return DefaultResponse.withCode(permissionService.getPermissionById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create permission")
    @PostMapping
    public ResponseEntity<?> createPermission(@Valid @RequestBody PermissionRequest request) {
        return DefaultResponse.withCode(permissionService.createPermission(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update permission")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionRequest request) {
        return DefaultResponse.withCode(permissionService.updatePermission(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete permission")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
