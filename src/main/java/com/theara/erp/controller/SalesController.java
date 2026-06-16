package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.SaleRequest;
import com.theara.erp.dto.request.SaleReturnRequest;
import com.theara.erp.dto.request.VoidInvoiceRequest;
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

    @Operation(summary = "List invoices", description = "Paginated sales history.")
    @GetMapping("/invoices")
    public ResponseEntity<?> getInvoices(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(salesService.getInvoices(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Void an invoice",
            description = "Reverses the sale: restocks each line into the given warehouse "
                    + "(and back into its medicine batch when applicable) and sets the invoice to VOID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invoice voided"),
            @ApiResponse(responseCode = "404", description = "Invoice or warehouse not found"),
            @ApiResponse(responseCode = "409", description = "Invoice already voided")
    })
    @PostMapping("/invoices/{id}/void")
    public ResponseEntity<?> voidInvoice(@PathVariable Long id, @Valid @RequestBody VoidInvoiceRequest request) {
        return DefaultResponse.withCode(salesService.voidInvoice(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Return / refund invoice lines",
            description = "Processes a partial or full sales return: restocks the returned quantity of each "
                    + "selected line into the given warehouse (and its medicine batch when applicable), refunds the "
                    + "proportional line value, and sets the invoice to PARTIALLY_REFUNDED or REFUNDED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Return processed"),
            @ApiResponse(responseCode = "400", description = "Line does not belong to the invoice"),
            @ApiResponse(responseCode = "404", description = "Invoice or warehouse not found"),
            @ApiResponse(responseCode = "409", description = "Return quantity exceeds returnable, or invoice voided/already refunded")
    })
    @PostMapping("/invoices/{id}/return")
    public ResponseEntity<?> returnSale(@PathVariable Long id, @Valid @RequestBody SaleReturnRequest request) {
        return DefaultResponse.withCode(salesService.returnSale(id, request), ErrorCode.SUCCESS);
    }
}
