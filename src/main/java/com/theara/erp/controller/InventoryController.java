package com.theara.erp.controller;

import com.theara.erp.common.PageMapper;
import com.theara.erp.constant.ErrorCode;
import com.theara.erp.constant.StockMovementType;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.StockAdjustRequest;
import com.theara.erp.dto.request.StockInRequest;
import com.theara.erp.dto.request.StockOutRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.dto.response.StockMovementResponse;
import com.theara.erp.dto.response.StockResponse;
import com.theara.erp.entity.Stock;
import com.theara.erp.entity.StockMovement;
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

    @Operation(summary = "Stock out (issue)",
            description = "Decreases on-hand quantity for a product in a warehouse (manual issue/write-off) and writes a ledger entry. "
                    + "Refuses to go below zero (409 Insufficient stock).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Stock issued"),
            @ApiResponse(responseCode = "404", description = "Warehouse or product not found"),
            @ApiResponse(responseCode = "409", description = "Insufficient stock")
    })
    @PostMapping("/stock-out")
    public ResponseEntity<?> stockOut(@Valid @RequestBody StockOutRequest request) {
        Stock stock = inventoryService.stockOut(
                request.getWarehouseId(), request.getProductId(), request.getQuantity(), request.getNote());
        return DefaultResponse.withCode(toResponse(stock), ErrorCode.CREATED);
    }

    @Operation(summary = "Adjust stock (stock-take)",
            description = "Sets on-hand to a physically counted quantity, posting the difference as an ADJUSTMENT ledger entry. "
                    + "The moving-average cost is preserved.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Stock adjusted"),
            @ApiResponse(responseCode = "400", description = "Counted quantity equals on-hand; nothing to adjust"),
            @ApiResponse(responseCode = "404", description = "Warehouse or product not found")
    })
    @PostMapping("/adjust")
    public ResponseEntity<?> adjust(@Valid @RequestBody StockAdjustRequest request) {
        Stock stock = inventoryService.adjust(
                request.getWarehouseId(), request.getProductId(), request.getCountedQuantity(), request.getNote());
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

    @Operation(summary = "List stock movements (ledger)",
            description = "Returns the paginated stock-movement ledger, optionally filtered by warehouse and/or product.")
    @GetMapping("/movements")
    public ResponseEntity<?> movements(@RequestParam(required = false) Long warehouseId,
                                       @RequestParam(required = false) Long productId,
                                       PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(
                PageMapper.toResponse(
                        inventoryService.getMovements(warehouseId, productId, request.getPageAble()),
                        InventoryController::toMovementResponse),
                ErrorCode.SUCCESS);
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

    private static StockMovementResponse toMovementResponse(StockMovement m) {
        return StockMovementResponse.builder()
                .id(m.getId())
                .warehouseId(m.getWarehouse().getId())
                .productId(m.getProduct().getId())
                .productName(m.getProduct().getName())
                .movementType(m.getMovementType())
                .quantity(m.getQuantity())
                .unitCost(m.getUnitCost())
                .referenceType(m.getReferenceType())
                .referenceId(m.getReferenceId())
                .note(m.getNote())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
