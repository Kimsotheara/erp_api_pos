package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.MenuItemRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/menu-items")
@Tag(name = "Menu Item", description = "Pub/Restaurant: menu management with happy-hour pricing")
@Slf4j
public class MenuItemController {
    private final MenuItemService menuItemService;

    @Operation(summary = "List menu items")
    @GetMapping
    public ResponseEntity<?> getMenuItems(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(menuItemService.getMenuItems(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get menu item by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getMenuItemById(@PathVariable Long id) {
        return DefaultResponse.withCode(menuItemService.getMenuItemById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create menu item")
    @PostMapping
    public ResponseEntity<?> createMenuItem(@Valid @RequestBody MenuItemRequest request) {
        return DefaultResponse.withCode(menuItemService.createMenuItem(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update menu item")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenuItem(@PathVariable Long id, @Valid @RequestBody MenuItemRequest request) {
        return DefaultResponse.withCode(menuItemService.updateMenuItem(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete menu item", description = "Soft-deletes a menu item.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
