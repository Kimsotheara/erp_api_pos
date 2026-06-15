package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.PaymentMethodRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.PaymentMethodResponse;
import com.theara.erp.entity.Company;
import com.theara.erp.entity.PaymentMethod;
import com.theara.erp.mapper.PaymentMethodMapper;
import com.theara.erp.repository.CompanyRepository;
import com.theara.erp.repository.PaymentMethodRepository;
import com.theara.erp.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;
    private final CompanyRepository companyRepository;
    private final PaymentMethodMapper paymentMethodMapper;

    @Override @Transactional
    public PaymentMethodResponse create(PaymentMethodRequest request) {
        PaymentMethod pm = new PaymentMethod();
        apply(pm, request);
        return paymentMethodMapper.toResponse(paymentMethodRepository.save(pm));
    }

    @Override @Transactional(readOnly = true)
    public PaymentMethodResponse getById(Long id) {
        return paymentMethodMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public PaymentMethodResponse update(Long id, PaymentMethodRequest request) {
        PaymentMethod pm = findById(id);
        apply(pm, request);
        return paymentMethodMapper.toResponse(paymentMethodRepository.save(pm));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<PaymentMethod, PaymentMethodResponse, Void> getAll(PageAbleRequest<Void> request) {
        Page<PaymentMethod> page = paymentMethodRepository.findAll(request.getPageAble());
        List<PaymentMethodResponse> list = page.getContent().stream().map(paymentMethodMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void delete(Long id) {
        PaymentMethod pm = findById(id);
        pm.setIsDeleted(1);
        paymentMethodRepository.save(pm);
    }

    private void apply(PaymentMethod pm, PaymentMethodRequest r) {
        Company company = companyRepository.findById(r.getCompanyId())
                .orElseThrow(() -> notFound("Company"));
        pm.setCompany(company);
        pm.setName(r.getName());
        if (r.getIsCash() != null) pm.setIsCash(r.getIsCash());
        if (r.getIsActive() != null) pm.setIsActive(r.getIsActive());
    }

    private PaymentMethod findById(Long id) {
        return paymentMethodRepository.findById(id).orElseThrow(() -> notFound("PaymentMethod"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
