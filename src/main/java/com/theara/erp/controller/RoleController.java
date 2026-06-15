package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.RoleRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
@Tag(name = "Role", description = "Role management with permission assignment")
@Slf4j
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "List roles")
    @GetMapping
    public ResponseEntity<?> getRoles(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(roleService.getRoles(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get role by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        return DefaultResponse.withCode(roleService.getRoleById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create role", description = "Optionally grants permissions via permissionIds.")
    @PostMapping
    public ResponseEntity<?> createRole(@Valid @RequestBody RoleRequest request) {
        return DefaultResponse.withCode(roleService.createRole(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update role", description = "Replaces the role's permission set with permissionIds.")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        return DefaultResponse.withCode(roleService.updateRole(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete role", description = "Soft-deletes a role (system roles are protected).")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
