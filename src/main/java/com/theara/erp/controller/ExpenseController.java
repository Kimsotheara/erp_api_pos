package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.ExpenseRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/expenses")
@Tag(name = "Expense", description = "Operating expense tracking")
@Slf4j
public class ExpenseController {
    private final ExpenseService expenseService;

    @Operation(summary = "List expenses")
    @GetMapping
    public ResponseEntity<?> getExpenses(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(expenseService.getExpenses(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get expense by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getExpenseById(@PathVariable Long id) {
        return DefaultResponse.withCode(expenseService.getExpenseById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create expense")
    @PostMapping
    public ResponseEntity<?> createExpense(@Valid @RequestBody ExpenseRequest request) {
        return DefaultResponse.withCode(expenseService.createExpense(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update expense")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        return DefaultResponse.withCode(expenseService.updateExpense(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete expense", description = "Soft-deletes an expense record.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
