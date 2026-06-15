package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.BranchRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.BranchResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Branch;
import com.theara.erp.entity.Company;
import com.theara.erp.mapper.BranchMapper;
import com.theara.erp.repository.BranchRepository;
import com.theara.erp.repository.CompanyRepository;
import com.theara.erp.repository.CurrencyRepository;
import com.theara.erp.service.BranchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final CompanyRepository companyRepository;
    private final CurrencyRepository currencyRepository;
    private final BranchMapper branchMapper;

    @Override @Transactional
    public BranchResponse createBranch(BranchRequest request) {
        if (branchRepository.existsByCompanyIdAndCodeIgnoreCase(request.getCompanyId(), request.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Branch code '" + request.getCode() + "' " + ErrorCode.CODE_ALREADY_EXISTS.getDescription());
        }
        Branch branch = new Branch();
        apply(branch, request);
        return branchMapper.toResponse(branchRepository.save(branch));
    }

    @Override @Transactional(readOnly = true)
    public BranchResponse getBranchById(Long id) {
        return branchMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public BranchResponse updateBranch(Long id, BranchRequest request) {
        Branch branch = findById(id);
        if (branchRepository.existsByCompanyIdAndCodeIgnoreCaseAndIdNot(request.getCompanyId(), request.getCode(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Branch code '" + request.getCode() + "' " + ErrorCode.CODE_ALREADY_EXISTS.getDescription());
        }
        apply(branch, request);
        return branchMapper.toResponse(branchRepository.save(branch));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<Branch, BranchResponse, Void> getBranches(PageAbleRequest<Void> request) {
        Page<Branch> page = branchRepository.findAll(request.getPageAble());
        List<BranchResponse> list = page.getContent().stream().map(branchMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deleteBranch(Long id) {
        Branch branch = findById(id);
        branch.setIsDeleted(1);
        branchRepository.save(branch);
    }

    private void apply(Branch b, BranchRequest r) {
        Company company = companyRepository.findById(r.getCompanyId())
                .orElseThrow(() -> notFound("Company"));
        b.setCompany(company);
        b.setCode(r.getCode());
        b.setName(r.getName());
        b.setPhone(r.getPhone());
        b.setAddress(r.getAddress());
        b.setCurrency(r.getCurrencyId() == null ? null
                : currencyRepository.findById(r.getCurrencyId()).orElseThrow(() -> notFound("Currency")));
        if (r.getIsActive() != null) b.setIsActive(r.getIsActive());
    }

    private Branch findById(Long id) {
        return branchRepository.findById(id).orElseThrow(() -> notFound("Branch"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
