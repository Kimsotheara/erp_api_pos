package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.CashMovementRequest;
import com.theara.erp.dto.request.CloseCashDrawerRequest;
import com.theara.erp.dto.request.OpenCashDrawerRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.CashDrawerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cash-drawers")
@Tag(name = "Cash Drawer", description = "Cashier shift cash management (open / movement / close)")
@Slf4j
public class CashDrawerController {

    private final CashDrawerService cashDrawerService;

    @Operation(summary = "List cash drawers")
    @GetMapping
    public ResponseEntity<?> getDrawers(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(cashDrawerService.getDrawers(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get cash drawer by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getDrawerById(@PathVariable Long id) {
        return DefaultResponse.withCode(cashDrawerService.getDrawerById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Open a cash drawer", description = "Starts a cash session with an opening float.")
    @PostMapping("/open")
    public ResponseEntity<?> openDrawer(@Valid @RequestBody OpenCashDrawerRequest request) {
        return DefaultResponse.withCode(cashDrawerService.openDrawer(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Record a cash movement", description = "Pay-in / pay-out against an open drawer.")
    @PostMapping("/{id}/movements")
    public ResponseEntity<?> addMovement(@PathVariable Long id, @Valid @RequestBody CashMovementRequest request) {
        return DefaultResponse.withCode(cashDrawerService.addMovement(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Close a cash drawer", description = "Counts cash and records the over/short difference.")
    @PostMapping("/{id}/close")
    public ResponseEntity<?> closeDrawer(@PathVariable Long id, @Valid @RequestBody CloseCashDrawerRequest request) {
        return DefaultResponse.withCode(cashDrawerService.closeDrawer(id, request), ErrorCode.SUCCESS);
    }
}
