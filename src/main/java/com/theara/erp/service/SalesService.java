package com.theara.erp.service;

import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.SaleRequest;
import com.theara.erp.dto.request.SaleReturnRequest;
import com.theara.erp.dto.request.VoidInvoiceRequest;
import com.theara.erp.dto.response.InvoiceResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Invoice;

public interface SalesService {
    InvoiceResponse checkout(SaleRequest request);

    InvoiceResponse getInvoiceById(Long id);

    PageAbleResponse<Invoice, InvoiceResponse, Void> getInvoices(PageAbleRequest<Void> request);

    InvoiceResponse voidInvoice(Long id, VoidInvoiceRequest request);

    InvoiceResponse returnSale(Long id, SaleReturnRequest request);
}
