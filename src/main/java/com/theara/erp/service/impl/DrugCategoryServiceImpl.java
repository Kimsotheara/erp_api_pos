package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.DrugCategoryRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.DrugCategoryResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Company;
import com.theara.erp.entity.DrugCategory;
import com.theara.erp.mapper.DrugCategoryMapper;
import com.theara.erp.repository.CompanyRepository;
import com.theara.erp.repository.DrugCategoryRepository;
import com.theara.erp.service.DrugCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class DrugCategoryServiceImpl implements DrugCategoryService {

    private final DrugCategoryRepository drugCategoryRepository;
    private final CompanyRepository companyRepository;
    private final DrugCategoryMapper drugCategoryMapper;

    @Override @Transactional
    public DrugCategoryResponse createDrugCategory(DrugCategoryRequest request) {
        DrugCategory drugCategory = new DrugCategory();
        apply(drugCategory, request);
        return drugCategoryMapper.toResponse(drugCategoryRepository.save(drugCategory));
    }

    @Override @Transactional(readOnly = true)
    public DrugCategoryResponse getDrugCategoryById(Long id) {
        return drugCategoryMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public DrugCategoryResponse updateDrugCategory(Long id, DrugCategoryRequest request) {
        DrugCategory drugCategory = findById(id);
        apply(drugCategory, request);
        return drugCategoryMapper.toResponse(drugCategoryRepository.save(drugCategory));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<DrugCategory, DrugCategoryResponse, Void> getDrugCategories(PageAbleRequest<Void> request) {
        Page<DrugCategory> page = drugCategoryRepository.findAll(request.getPageAble());
        List<DrugCategoryResponse> list = page.getContent().stream().map(drugCategoryMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deleteDrugCategory(Long id) {
        DrugCategory drugCategory = findById(id);
        drugCategory.setIsDeleted(1);
        drugCategoryRepository.save(drugCategory);
    }

    private void apply(DrugCategory d, DrugCategoryRequest r) {
        Company company = companyRepository.findById(r.getCompanyId()).orElseThrow(() -> notFound("Company"));
        d.setCompany(company);
        d.setName(r.getName());
        if (r.getRequiresPrescription() != null) d.setRequiresPrescription(r.getRequiresPrescription());
    }

    private DrugCategory findById(Long id) {
        return drugCategoryRepository.findById(id).orElseThrow(() -> notFound("DrugCategory"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
