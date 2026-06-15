package com.theara.erp.service;

import com.theara.erp.dto.request.BrandRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.BrandResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Brand;

public interface BrandService {
    BrandResponse createBrand(BrandRequest request);
    BrandResponse getBrandById(Long id);
    BrandResponse updateBrand(Long id, BrandRequest request);
    PageAbleResponse<Brand, BrandResponse, Void> getBrands(PageAbleRequest<Void> request);
    void deleteBrand(Long id);
}
