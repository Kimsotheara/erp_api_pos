package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.IncomeRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.IncomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/incomes")
@Tag(name = "Income", description = "Other income tracking")
@Slf4j
public class IncomeController {
    private final IncomeService incomeService;

    @Operation(summary = "List incomes")
    @GetMapping
    public ResponseEntity<?> getIncomes(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(incomeService.getIncomes(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get income by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getIncomeById(@PathVariable Long id) {
        return DefaultResponse.withCode(incomeService.getIncomeById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create income")
    @PostMapping
    public ResponseEntity<?> createIncome(@Valid @RequestBody IncomeRequest request) {
        return DefaultResponse.withCode(incomeService.createIncome(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update income")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateIncome(@PathVariable Long id, @Valid @RequestBody IncomeRequest request) {
        return DefaultResponse.withCode(incomeService.updateIncome(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete income", description = "Soft-deletes an income record.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncome(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
