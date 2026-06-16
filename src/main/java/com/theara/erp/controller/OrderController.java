package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.AddOrderItemsRequest;
import com.theara.erp.dto.request.OpenTableRequest;
import com.theara.erp.dto.request.SettleBillRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Tag(name = "Dine-in Order", description = "Pub/Restaurant: open table -> add items (KOT) -> settle bill")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Open a table", description = "Starts an OPEN bill for a table and marks it OCCUPIED.")
    @PostMapping("/open")
    public ResponseEntity<?> openTable(@Valid @RequestBody OpenTableRequest request) {
        return DefaultResponse.withCode(orderService.openTable(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Add items / fire to kitchen",
            description = "Adds lines to the open bill (happy-hour pricing when happyHour=true) and creates a kitchen ticket.")
    @PostMapping("/{invoiceId}/items")
    public ResponseEntity<?> addItems(@PathVariable Long invoiceId, @Valid @RequestBody AddOrderItemsRequest request) {
        return DefaultResponse.withCode(orderService.addItems(invoiceId, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get the running bill")
    @GetMapping("/{invoiceId}")
    public ResponseEntity<?> getBill(@PathVariable Long invoiceId) {
        return DefaultResponse.withCode(orderService.getBill(invoiceId), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Settle the bill",
            description = "Takes payment, finalizes the invoice, deducts stock and frees the table.")
    @PostMapping("/{invoiceId}/settle")
    public ResponseEntity<?> settle(@PathVariable Long invoiceId, @Valid @RequestBody SettleBillRequest request) {
        return DefaultResponse.withCode(orderService.settle(invoiceId, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Cancel the order", description = "Voids an unpaid bill and frees the table.")
    @PostMapping("/{invoiceId}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long invoiceId) {
        return DefaultResponse.withCode(orderService.cancel(invoiceId), ErrorCode.SUCCESS);
    }
}
