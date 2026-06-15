package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.BrandRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.BrandResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Brand;
import com.theara.erp.entity.Company;
import com.theara.erp.mapper.BrandMapper;
import com.theara.erp.repository.BrandRepository;
import com.theara.erp.repository.CompanyRepository;
import com.theara.erp.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final CompanyRepository companyRepository;
    private final BrandMapper brandMapper;

    @Override @Transactional
    public BrandResponse createBrand(BrandRequest request) {
        Brand brand = new Brand();
        apply(brand, request);
        return brandMapper.toResponse(brandRepository.save(brand));
    }

    @Override @Transactional(readOnly = true)
    public BrandResponse getBrandById(Long id) {
        return brandMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public BrandResponse updateBrand(Long id, BrandRequest request) {
        Brand brand = findById(id);
        apply(brand, request);
        return brandMapper.toResponse(brandRepository.save(brand));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<Brand, BrandResponse, Void> getBrands(PageAbleRequest<Void> request) {
        Page<Brand> page = brandRepository.findAll(request.getPageAble());
        List<BrandResponse> list = page.getContent().stream().map(brandMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deleteBrand(Long id) {
        Brand brand = findById(id);
        brand.setIsDeleted(1);
        brandRepository.save(brand);
    }

    private void apply(Brand b, BrandRequest r) {
        Company company = companyRepository.findById(r.getCompanyId())
                .orElseThrow(() -> notFound("Company"));
        b.setCompany(company);
        b.setName(r.getName());
        if (r.getIsActive() != null) b.setIsActive(r.getIsActive());
    }

    private Brand findById(Long id) {
        return brandRepository.findById(id).orElseThrow(() -> notFound("Brand"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
