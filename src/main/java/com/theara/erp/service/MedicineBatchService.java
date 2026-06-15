package com.theara.erp.service;

import com.theara.erp.dto.request.MedicineBatchRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.MedicineBatchResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.MedicineBatch;

public interface MedicineBatchService {
    MedicineBatchResponse createBatch(MedicineBatchRequest request);
    MedicineBatchResponse getBatchById(Long id);
    PageAbleResponse<MedicineBatch, MedicineBatchResponse, Void> getBatches(PageAbleRequest<Void> request);
    /** Batches expiring within {@code withinDays} that still hold stock. */
    PageAbleResponse<MedicineBatch, MedicineBatchResponse, Void> getExpiringBatches(int withinDays, PageAbleRequest<Void> request);
    void deleteBatch(Long id);
}
