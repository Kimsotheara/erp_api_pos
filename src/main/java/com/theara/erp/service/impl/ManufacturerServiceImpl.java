package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.ManufacturerRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.ManufacturerResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Company;
import com.theara.erp.entity.Manufacturer;
import com.theara.erp.mapper.ManufacturerMapper;
import com.theara.erp.repository.CompanyRepository;
import com.theara.erp.repository.ManufacturerRepository;
import com.theara.erp.service.ManufacturerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class ManufacturerServiceImpl implements ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;
    private final CompanyRepository companyRepository;
    private final ManufacturerMapper manufacturerMapper;

    @Override @Transactional
    public ManufacturerResponse createManufacturer(ManufacturerRequest request) {
        Manufacturer manufacturer = new Manufacturer();
        apply(manufacturer, request);
        return manufacturerMapper.toResponse(manufacturerRepository.save(manufacturer));
    }

    @Override @Transactional(readOnly = true)
    public ManufacturerResponse getManufacturerById(Long id) {
        return manufacturerMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public ManufacturerResponse updateManufacturer(Long id, ManufacturerRequest request) {
        Manufacturer manufacturer = findById(id);
        apply(manufacturer, request);
        return manufacturerMapper.toResponse(manufacturerRepository.save(manufacturer));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<Manufacturer, ManufacturerResponse, Void> getManufacturers(PageAbleRequest<Void> request) {
        Page<Manufacturer> page = manufacturerRepository.findAll(request.getPageAble());
        List<ManufacturerResponse> list = page.getContent().stream().map(manufacturerMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deleteManufacturer(Long id) {
        Manufacturer manufacturer = findById(id);
        manufacturer.setIsDeleted(1);
        manufacturerRepository.save(manufacturer);
    }

    private void apply(Manufacturer m, ManufacturerRequest r) {
        Company company = companyRepository.findById(r.getCompanyId()).orElseThrow(() -> notFound("Company"));
        m.setCompany(company);
        m.setName(r.getName());
        m.setCountry(r.getCountry());
        if (r.getIsActive() != null) m.setIsActive(r.getIsActive());
    }

    private Manufacturer findById(Long id) {
        return manufacturerRepository.findById(id).orElseThrow(() -> notFound("Manufacturer"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
