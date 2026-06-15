package com.theara.erp.service;

import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.SupplierRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.SupplierResponse;
import com.theara.erp.entity.Supplier;

public interface SupplierService {
    SupplierResponse createSupplier(SupplierRequest request);
    SupplierResponse getSupplierById(Long id);
    SupplierResponse updateSupplier(Long id, SupplierRequest request);
    PageAbleResponse<Supplier, SupplierResponse, Void> getSuppliers(PageAbleRequest<Void> request);
    void deleteSupplier(Long id);
}
