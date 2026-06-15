package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.SupplierContactRequest;
import com.theara.erp.dto.request.SupplierRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.SupplierResponse;
import com.theara.erp.entity.Company;
import com.theara.erp.entity.Supplier;
import com.theara.erp.entity.SupplierContact;
import com.theara.erp.mapper.SupplierMapper;
import com.theara.erp.repository.CompanyRepository;
import com.theara.erp.repository.SupplierRepository;
import com.theara.erp.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final CompanyRepository companyRepository;
    private final SupplierMapper supplierMapper;

    @Override @Transactional
    public SupplierResponse createSupplier(SupplierRequest request) {
        if (request.getCode() != null
                && supplierRepository.existsByCompanyIdAndCodeIgnoreCase(request.getCompanyId(), request.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Supplier code '" + request.getCode() + "' " + ErrorCode.CODE_ALREADY_EXISTS.getDescription());
        }
        Supplier supplier = new Supplier();
        apply(supplier, request);
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    @Override @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(Long id) {
        return supplierMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier supplier = findById(id);
        if (request.getCode() != null
                && supplierRepository.existsByCompanyIdAndCodeIgnoreCaseAndIdNot(request.getCompanyId(), request.getCode(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Supplier code '" + request.getCode() + "' " + ErrorCode.CODE_ALREADY_EXISTS.getDescription());
        }
        apply(supplier, request);
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<Supplier, SupplierResponse, Void> getSuppliers(PageAbleRequest<Void> request) {
        return com.theara.erp.common.PageMapper.toResponseWithoutImage(
                supplierRepository.findAll(request.getPageAble()), supplierMapper::toResponse);
    }

    @Override @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = findById(id);
        supplier.setIsDeleted(1);
        supplierRepository.save(supplier);
    }

    private void apply(Supplier s, SupplierRequest r) {
        Company company = companyRepository.findById(r.getCompanyId())
                .orElseThrow(() -> notFound("Company"));
        s.setCompany(company);
        s.setCode(r.getCode());
        s.setName(r.getName());
        s.setTaxNumber(r.getTaxNumber());
        s.setPhone(r.getPhone());
        s.setEmail(r.getEmail());
        s.setAddress(r.getAddress());
        s.setImage(r.getImage());
        if (r.getIsActive() != null) s.setIsActive(r.getIsActive());

        // Replace the contact set wholesale (orphanRemoval clears the previous rows).
        s.getContacts().clear();
        if (r.getContacts() != null) {
            for (SupplierContactRequest c : r.getContacts()) {
                s.addContact(SupplierContact.builder()
                        .name(c.getName())
                        .position(c.getPosition())
                        .phone(c.getPhone())
                        .email(c.getEmail())
                        .build());
            }
        }
    }

    private Supplier findById(Long id) {
        return supplierRepository.findById(id).orElseThrow(() -> notFound("Supplier"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
