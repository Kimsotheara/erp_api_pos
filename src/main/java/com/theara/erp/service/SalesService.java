package com.theara.erp.service;

import com.theara.erp.dto.request.SaleRequest;
import com.theara.erp.dto.response.InvoiceResponse;

public interface SalesService {
    InvoiceResponse checkout(SaleRequest request);

    InvoiceResponse getInvoiceById(Long id);
}
