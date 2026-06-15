package com.theara.erp.service;

import com.theara.erp.dto.request.GoodsReceiptRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.GoodsReceiptResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.GoodsReceipt;

public interface GoodsReceiptService {
    GoodsReceiptResponse receiveGoods(GoodsReceiptRequest request);
    GoodsReceiptResponse getGoodsReceiptById(Long id);
    PageAbleResponse<GoodsReceipt, GoodsReceiptResponse, Void> getGoodsReceipts(PageAbleRequest<Void> request);
}
