package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.CustomerRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer", description = "Customer management")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "List customers")
    @GetMapping
    public ResponseEntity<?> list(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(customerService.getCustomers(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get customer by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return DefaultResponse.withCode(customerService.getCustomerById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create customer")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CustomerRequest request) {
        return DefaultResponse.withCode(customerService.createCustomer(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update customer")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return DefaultResponse.withCode(customerService.updateCustomer(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete customer (soft)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
