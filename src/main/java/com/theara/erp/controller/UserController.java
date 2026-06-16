package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.ActiveStatusRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.UserRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "User management with role and branch assignment")
@Slf4j
public class UserController {
    private final UserService userService;

    @Operation(summary = "List users")
    @GetMapping
    public ResponseEntity<?> getUsers(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(userService.getUsers(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return DefaultResponse.withCode(userService.getUserById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create user", description = "Password is BCrypt-hashed; assign roles/branches by id.")
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest request) {
        return DefaultResponse.withCode(userService.createUser(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update user", description = "Leave password blank to keep the current password.")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return DefaultResponse.withCode(userService.updateUser(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Activate/deactivate user", description = "Toggles the user's active status.")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> setUserStatus(@PathVariable Long id, @Valid @RequestBody ActiveStatusRequest request) {
        return DefaultResponse.withCode(userService.setActiveStatus(id, request.getIsActive()), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete user", description = "Soft-deletes a user.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
