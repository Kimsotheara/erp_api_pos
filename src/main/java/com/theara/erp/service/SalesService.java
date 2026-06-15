package com.theara.erp.service;

import com.theara.erp.dto.request.SaleRequest;
import com.theara.erp.dto.response.InvoiceResponse;

public interface SalesService {

    /**
     * POS checkout: validate the cart, price each line, compute tax/discount/total,
     * persist the invoice with its items and payments, then deduct stock through
     * the {@link InventoryService}. Runs in a single transaction.
     */
    InvoiceResponse checkout(SaleRequest request);

    InvoiceResponse getInvoiceById(Long id);
}
