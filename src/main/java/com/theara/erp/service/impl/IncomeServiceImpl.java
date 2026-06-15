package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.IncomeRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.IncomeResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Income;
import com.theara.erp.mapper.IncomeMapper;
import com.theara.erp.repository.BranchRepository;
import com.theara.erp.repository.CompanyRepository;
import com.theara.erp.repository.IncomeRepository;
import com.theara.erp.service.IncomeService;
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
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;
    private final IncomeMapper incomeMapper;

    @Override @Transactional
    public IncomeResponse createIncome(IncomeRequest request) {
        Income income = new Income();
        apply(income, request);
        return incomeMapper.toResponse(incomeRepository.save(income));
    }

    @Override @Transactional(readOnly = true)
    public IncomeResponse getIncomeById(Long id) {
        return incomeMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public IncomeResponse updateIncome(Long id, IncomeRequest request) {
        Income income = findById(id);
        apply(income, request);
        return incomeMapper.toResponse(incomeRepository.save(income));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<Income, IncomeResponse, Void> getIncomes(PageAbleRequest<Void> request) {
        Page<Income> page = incomeRepository.findAll(request.getPageAble());
        List<IncomeResponse> list = page.getContent().stream().map(incomeMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deleteIncome(Long id) {
        Income income = findById(id);
        income.setIsDeleted(1);
        incomeRepository.save(income);
    }

    private void apply(Income i, IncomeRequest r) {
        i.setCompany(companyRepository.findById(r.getCompanyId()).orElseThrow(() -> notFound("Company")));
        i.setBranch(r.getBranchId() == null ? null
                : branchRepository.findById(r.getBranchId()).orElseThrow(() -> notFound("Branch")));
        i.setCategory(r.getCategory());
        i.setAmount(r.getAmount());
        i.setDescription(r.getDescription());
        i.setIncomeDate(r.getIncomeDate() != null ? r.getIncomeDate() : LocalDate.now());
        i.setCreatedBy(r.getCreatedBy());
    }

    private Income findById(Long id) {
        return incomeRepository.findById(id).orElseThrow(() -> notFound("Income"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
