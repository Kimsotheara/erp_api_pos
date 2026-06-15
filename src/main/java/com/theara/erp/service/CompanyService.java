package com.theara.erp.service;

import com.theara.erp.dto.request.CompanyRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.CompanyResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Company;

public interface CompanyService {

    CompanyResponse createCompany(CompanyRequest request);

    CompanyResponse getCompanyById(Long id);

    CompanyResponse updateCompany(Long id, CompanyRequest request);

    PageAbleResponse<Company, CompanyResponse, Void> getCompanies(PageAbleRequest<Void> request);

    void deleteCompany(Long id);
}
