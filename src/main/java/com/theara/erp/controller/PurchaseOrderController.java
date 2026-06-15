package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.ApprovePurchaseOrderRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.PurchaseOrderRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/purchase-orders")
@Tag(name = "Purchase Order", description = "Procurement: purchase orders, approval workflow")
@Slf4j
public class PurchaseOrderController {
    private final PurchaseOrderService purchaseOrderService;

    @Operation(summary = "List purchase orders")
    @GetMapping
    public ResponseEntity<?> getPurchaseOrders(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(purchaseOrderService.getPurchaseOrders(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get purchase order by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getPurchaseOrderById(@PathVariable Long id) {
        return DefaultResponse.withCode(purchaseOrderService.getPurchaseOrderById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create purchase order", description = "Creates a DRAFT purchase order.")
    @PostMapping
    public ResponseEntity<?> createPurchaseOrder(@Valid @RequestBody PurchaseOrderRequest request) {
        return DefaultResponse.withCode(purchaseOrderService.createPurchaseOrder(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update purchase order", description = "Editable only while DRAFT or REQUESTED.")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePurchaseOrder(@PathVariable Long id, @Valid @RequestBody PurchaseOrderRequest request) {
        return DefaultResponse.withCode(purchaseOrderService.updatePurchaseOrder(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Submit purchase order for approval", description = "DRAFT -> REQUESTED.")
    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitPurchaseOrder(@PathVariable Long id) {
        return DefaultResponse.withCode(purchaseOrderService.submitPurchaseOrder(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Approve or reject purchase order", description = "REQUESTED -> APPROVED/REJECTED.")
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approvePurchaseOrder(@PathVariable Long id,
                                                  @Valid @RequestBody ApprovePurchaseOrderRequest request) {
        return DefaultResponse.withCode(purchaseOrderService.approvePurchaseOrder(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Cancel purchase order")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelPurchaseOrder(@PathVariable Long id) {
        return DefaultResponse.withCode(purchaseOrderService.cancelPurchaseOrder(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete purchase order", description = "Soft-deletes a purchase order.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePurchaseOrder(@PathVariable Long id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
