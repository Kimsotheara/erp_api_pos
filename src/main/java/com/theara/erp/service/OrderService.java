package com.theara.erp.service;

import com.theara.erp.dto.request.AddOrderItemsRequest;
import com.theara.erp.dto.request.OpenTableRequest;
import com.theara.erp.dto.request.SettleBillRequest;
import com.theara.erp.dto.response.InvoiceResponse;

public interface OrderService {
    InvoiceResponse openTable(OpenTableRequest request);
    InvoiceResponse addItems(Long invoiceId, AddOrderItemsRequest request);
    InvoiceResponse getBill(Long invoiceId);
    InvoiceResponse settle(Long invoiceId, SettleBillRequest request);
    InvoiceResponse cancel(Long invoiceId);
}
