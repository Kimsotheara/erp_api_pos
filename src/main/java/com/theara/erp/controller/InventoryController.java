package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.constant.StockMovementType;
import com.theara.erp.dto.request.StockInRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.dto.response.StockResponse;
import com.theara.erp.entity.Stock;
import com.theara.erp.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory", description = "Stock receipts and on-hand queries")
public class InventoryController {
    private final InventoryService inventoryService;

    @Operation(summary = "Stock in (receive)",
            description = "Increases on-hand quantity for a product in a warehouse and writes a ledger entry.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Stock received"),
            @ApiResponse(responseCode = "404", description = "Warehouse or product not found")
    })
    @PostMapping("/stock-in")
    public ResponseEntity<?> stockIn(@Valid @RequestBody StockInRequest request) {
        Stock stock = inventoryService.applyMovement(
                request.getWarehouseId(), request.getProductId(), request.getQuantity(),
                StockMovementType.IN, request.getUnitCost(), "MANUAL", null, request.getNote());
        return DefaultResponse.withCode(toResponse(stock), ErrorCode.CREATED);
    }

    @Operation(summary = "Get on-hand quantity")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "On-hand found"),
            @ApiResponse(responseCode = "404", description = "No stock row for product in warehouse")
    })
    @GetMapping("/on-hand")
    public ResponseEntity<?> onHand(@RequestParam Long warehouseId, @RequestParam Long productId) {
        return DefaultResponse.withCode(toResponse(inventoryService.getOnHand(warehouseId, productId)), ErrorCode.SUCCESS);
    }

    private StockResponse toResponse(Stock s) {
        return StockResponse.builder()
                .warehouseId(s.getWarehouse().getId())
                .productId(s.getProduct().getId())
                .productName(s.getProduct().getName())
                .quantity(s.getQuantity())
                .avgCost(s.getAvgCost())
                .build();
    }
}
