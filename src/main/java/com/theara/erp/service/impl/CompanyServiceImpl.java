package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.CompanyRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.CompanyResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Company;
import com.theara.erp.mapper.CompanyMapper;
import com.theara.erp.repository.CompanyRepository;
import com.theara.erp.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Override
    @Transactional
    public CompanyResponse createCompany(CompanyRequest request) {
        if (companyRepository.existsByNameIgnoreCase(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Company name '" + request.getName() + "' " + ErrorCode.ALREADY_EXISTS.getDescription());
        }
        Company company = companyMapper.toEntity(request);
        if (request.getIsActive() != null) {
            company.setIsActive(request.getIsActive());
        }
        return companyMapper.toResponse(companyRepository.save(company));
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(Long id) {
        return companyMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    public CompanyResponse updateCompany(Long id, CompanyRequest request) {
        Company company = findById(id);
        if (companyRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Company name '" + request.getName() + "' " + ErrorCode.ALREADY_EXISTS.getDescription());
        }
        companyMapper.updateEntity(request, company);
        return companyMapper.toResponse(companyRepository.save(company));
    }

    @Override
    @Transactional(readOnly = true)
    public PageAbleResponse<Company, CompanyResponse, Void> getCompanies(PageAbleRequest<Void> request) {
        Page<Company> page = companyRepository.findAll(request.getPageAble());
        List<CompanyResponse> list = page.getContent().stream().map(companyMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override
    @Transactional
    public void deleteCompany(Long id) {
        Company company = findById(id);
        company.setIsDeleted(1);
        companyRepository.save(company);
    }

    private Company findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Company " + ErrorCode.NOT_FOUND.getDescription()));
    }
}
