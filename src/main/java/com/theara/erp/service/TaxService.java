package com.theara.erp.service;

import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.TaxRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.TaxResponse;
import com.theara.erp.entity.Tax;

public interface TaxService {
    TaxResponse createTax(TaxRequest request);
    TaxResponse getTaxById(Long id);
    TaxResponse updateTax(Long id, TaxRequest request);
    PageAbleResponse<Tax, TaxResponse, Void> getTaxes(PageAbleRequest<Void> request);
    void deleteTax(Long id);
}
