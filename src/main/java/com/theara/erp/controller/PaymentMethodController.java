package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.PaymentMethodRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.PaymentMethodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/payment-methods")
@Tag(name = "Payment Method", description = "Sales — payment method management")
public class PaymentMethodController {

    private final PaymentMethodService service;

    @Operation(summary = "List payment methods")
    @GetMapping
    public ResponseEntity<?> list(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(service.getAll(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get payment method by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return DefaultResponse.withCode(service.getById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create payment method")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PaymentMethodRequest request) {
        return DefaultResponse.withCode(service.create(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update payment method")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody PaymentMethodRequest request) {
        return DefaultResponse.withCode(service.update(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete payment method (soft)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
