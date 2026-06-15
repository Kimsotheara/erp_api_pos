package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.ExpenseRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.ExpenseResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Expense;
import com.theara.erp.mapper.ExpenseMapper;
import com.theara.erp.repository.BranchRepository;
import com.theara.erp.repository.CompanyRepository;
import com.theara.erp.repository.ExpenseRepository;
import com.theara.erp.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;
    private final ExpenseMapper expenseMapper;

    @Override @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request) {
        Expense expense = new Expense();
        apply(expense, request);
        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    @Override @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Long id) {
        return expenseMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public ExpenseResponse updateExpense(Long id, ExpenseRequest request) {
        Expense expense = findById(id);
        apply(expense, request);
        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<Expense, ExpenseResponse, Void> getExpenses(PageAbleRequest<Void> request) {
        Page<Expense> page = expenseRepository.findAll(request.getPageAble());
        List<ExpenseResponse> list = page.getContent().stream().map(expenseMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deleteExpense(Long id) {
        Expense expense = findById(id);
        expense.setIsDeleted(1);
        expenseRepository.save(expense);
    }

    private void apply(Expense e, ExpenseRequest r) {
        e.setCompany(companyRepository.findById(r.getCompanyId()).orElseThrow(() -> notFound("Company")));
        e.setBranch(r.getBranchId() == null ? null
                : branchRepository.findById(r.getBranchId()).orElseThrow(() -> notFound("Branch")));
        e.setCategory(r.getCategory());
        e.setAmount(r.getAmount());
        e.setDescription(r.getDescription());
        e.setExpenseDate(r.getExpenseDate() != null ? r.getExpenseDate() : LocalDate.now());
        e.setCreatedBy(r.getCreatedBy());
    }

    private Expense findById(Long id) {
        return expenseRepository.findById(id).orElseThrow(() -> notFound("Expense"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
