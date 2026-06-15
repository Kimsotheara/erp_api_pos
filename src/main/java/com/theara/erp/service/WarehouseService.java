package com.theara.erp.service;

import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.WarehouseRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.WarehouseResponse;
import com.theara.erp.entity.Warehouse;

public interface WarehouseService {
    WarehouseResponse create(WarehouseRequest request);
    WarehouseResponse getById(Long id);
    WarehouseResponse update(Long id, WarehouseRequest request);
    PageAbleResponse<Warehouse, WarehouseResponse, Void> getAll(PageAbleRequest<Void> request);
    void delete(Long id);
}
