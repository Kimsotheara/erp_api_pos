package com.theara.erp.service;

import com.theara.erp.dto.request.ExpenseRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.ExpenseResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Expense;

public interface ExpenseService {
    ExpenseResponse createExpense(ExpenseRequest request);
    ExpenseResponse getExpenseById(Long id);
    ExpenseResponse updateExpense(Long id, ExpenseRequest request);
    PageAbleResponse<Expense, ExpenseResponse, Void> getExpenses(PageAbleRequest<Void> request);
    void deleteExpense(Long id);
}
