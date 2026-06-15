package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.RestaurantTableRequest;
import com.theara.erp.dto.request.TableStatusRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.RestaurantTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tables")
@Tag(name = "Restaurant Table", description = "Pub/Restaurant: table management")
@Slf4j
public class RestaurantTableController {
    private final RestaurantTableService restaurantTableService;

    @Operation(summary = "List tables")
    @GetMapping
    public ResponseEntity<?> getTables(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(restaurantTableService.getTables(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get table by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTableById(@PathVariable Long id) {
        return DefaultResponse.withCode(restaurantTableService.getTableById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create table")
    @PostMapping
    public ResponseEntity<?> createTable(@Valid @RequestBody RestaurantTableRequest request) {
        return DefaultResponse.withCode(restaurantTableService.createTable(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update table")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTable(@PathVariable Long id, @Valid @RequestBody RestaurantTableRequest request) {
        return DefaultResponse.withCode(restaurantTableService.updateTable(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Update table status", description = "AVAILABLE / OCCUPIED / RESERVED / CLEANING.")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @Valid @RequestBody TableStatusRequest request) {
        return DefaultResponse.withCode(restaurantTableService.updateStatus(id, request.getStatus()), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete table", description = "Soft-deletes a table.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTable(@PathVariable Long id) {
        restaurantTableService.deleteTable(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
