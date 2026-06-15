package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.TaxRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.TaxResponse;
import com.theara.erp.entity.Company;
import com.theara.erp.entity.Tax;
import com.theara.erp.mapper.TaxMapper;
import com.theara.erp.repository.CompanyRepository;
import com.theara.erp.repository.TaxRepository;
import com.theara.erp.service.TaxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class TaxServiceImpl implements TaxService {

    private final TaxRepository taxRepository;
    private final CompanyRepository companyRepository;
    private final TaxMapper taxMapper;

    @Override @Transactional
    public TaxResponse createTax(TaxRequest request) {
        Tax tax = new Tax();
        apply(tax, request);
        return taxMapper.toResponse(taxRepository.save(tax));
    }

    @Override @Transactional(readOnly = true)
    public TaxResponse getTaxById(Long id) {
        return taxMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public TaxResponse updateTax(Long id, TaxRequest request) {
        Tax tax = findById(id);
        apply(tax, request);
        return taxMapper.toResponse(taxRepository.save(tax));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<Tax, TaxResponse, Void> getTaxes(PageAbleRequest<Void> request) {
        Page<Tax> page = taxRepository.findAll(request.getPageAble());
        List<TaxResponse> list = page.getContent().stream().map(taxMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deleteTax(Long id) {
        Tax tax = findById(id);
        tax.setIsDeleted(1);
        taxRepository.save(tax);
    }

    private void apply(Tax t, TaxRequest r) {
        Company company = companyRepository.findById(r.getCompanyId())
                .orElseThrow(() -> notFound("Company"));
        t.setCompany(company);
        t.setName(r.getName());
        t.setRate(r.getRate());
        if (r.getIsInclusive() != null) t.setIsInclusive(r.getIsInclusive());
        if (r.getIsActive() != null) t.setIsActive(r.getIsActive());
    }

    private Tax findById(Long id) {
        return taxRepository.findById(id).orElseThrow(() -> notFound("Tax"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
