package com.theara.erp.service;

import com.theara.erp.dto.request.ApprovePurchaseOrderRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.PurchaseOrderRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.PurchaseOrderResponse;
import com.theara.erp.entity.PurchaseOrder;

public interface PurchaseOrderService {
    PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request);
    PurchaseOrderResponse getPurchaseOrderById(Long id);
    PurchaseOrderResponse updatePurchaseOrder(Long id, PurchaseOrderRequest request);
    PurchaseOrderResponse submitPurchaseOrder(Long id);
    PurchaseOrderResponse approvePurchaseOrder(Long id, ApprovePurchaseOrderRequest request);
    PurchaseOrderResponse cancelPurchaseOrder(Long id);
    PageAbleResponse<PurchaseOrder, PurchaseOrderResponse, Void> getPurchaseOrders(PageAbleRequest<Void> request);
    void deletePurchaseOrder(Long id);
}
