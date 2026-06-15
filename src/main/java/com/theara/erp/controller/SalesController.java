package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.SaleRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.SalesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sales")
@Tag(name = "Sales / POS", description = "Point-of-sale checkout and invoices")
@Slf4j
public class SalesController {
    private final SalesService salesService;

    @Operation(summary = "Checkout (create sale)",
            description = "Prices the cart, computes tax/discount/total, persists the invoice with "
                    + "payments, and deducts stock. Returns the completed invoice/receipt.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sale completed"),
            @ApiResponse(responseCode = "400", description = "Invalid cart / pricing"),
            @ApiResponse(responseCode = "404", description = "Referenced entity not found"),
            @ApiResponse(responseCode = "409", description = "Insufficient stock")
    })
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@Valid @RequestBody SaleRequest request) {
        return DefaultResponse.withCode(salesService.checkout(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Get invoice by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invoice found"),
            @ApiResponse(responseCode = "404", description = "Invoice not found")
    })
    @GetMapping("/invoices/{id}")
    public ResponseEntity<?> getInvoice(@PathVariable Long id) {
        return DefaultResponse.withCode(salesService.getInvoiceById(id), ErrorCode.SUCCESS);
    }
}
