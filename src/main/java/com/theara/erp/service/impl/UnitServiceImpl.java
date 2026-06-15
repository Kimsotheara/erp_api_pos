package com.theara.erp.service.impl;

import com.theara.erp.common.PageMapper;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.UnitRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.UnitResponse;
import com.theara.erp.entity.Company;
import com.theara.erp.entity.Unit;
import com.theara.erp.exception.ApiException;
import com.theara.erp.mapper.UnitMapper;
import com.theara.erp.repository.CompanyRepository;
import com.theara.erp.repository.UnitRepository;
import com.theara.erp.service.UnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j @Service @RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {
    private final UnitRepository unitRepository;
    private final CompanyRepository companyRepository;
    private final UnitMapper unitMapper;

    @Override @Transactional
    public UnitResponse createUnit(UnitRequest request) {
        Unit unit = new Unit();
        apply(unit, request);
        return unitMapper.toResponse(unitRepository.save(unit));
    }

    @Override @Transactional(readOnly = true)
    public UnitResponse getUnitById(Long id) {
        return unitMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public UnitResponse updateUnit(Long id, UnitRequest request) {
        Unit unit = findById(id);
        apply(unit, request);
        return unitMapper.toResponse(unitRepository.save(unit));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<Unit, UnitResponse, Void> getUnits(PageAbleRequest<Void> request) {
        return PageMapper.toResponse(unitRepository.findAll(request.getPageAble()), unitMapper::toResponse);
    }

    @Override @Transactional
    public void deleteUnit(Long id) {
        Unit unit = findById(id);
        unit.setIsDeleted(1);
        unitRepository.save(unit);
    }

    private void apply(Unit u, UnitRequest r) {
        Company company = companyRepository.findById(r.getCompanyId())
                .orElseThrow(() -> ApiException.notFound("Company"));
        u.setCompany(company);
        u.setName(r.getName());
        u.setAbbreviation(r.getAbbreviation());
        if (r.getIsActive() != null) u.setIsActive(r.getIsActive());
    }

    private Unit findById(Long id) {
        return unitRepository.findById(id).orElseThrow(() -> ApiException.notFound("Unit"));
    }
}
