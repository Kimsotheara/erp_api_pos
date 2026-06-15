package com.theara.erp.service;

import com.theara.erp.constant.TableStatus;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.RestaurantTableRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.RestaurantTableResponse;
import com.theara.erp.entity.RestaurantTable;

public interface RestaurantTableService {
    RestaurantTableResponse createTable(RestaurantTableRequest request);
    RestaurantTableResponse getTableById(Long id);
    RestaurantTableResponse updateTable(Long id, RestaurantTableRequest request);
    RestaurantTableResponse updateStatus(Long id, TableStatus status);
    PageAbleResponse<RestaurantTable, RestaurantTableResponse, Void> getTables(PageAbleRequest<Void> request);
    void deleteTable(Long id);
}
