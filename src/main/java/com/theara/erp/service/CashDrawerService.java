package com.theara.erp.service;

import com.theara.erp.dto.request.CashMovementRequest;
import com.theara.erp.dto.request.CloseCashDrawerRequest;
import com.theara.erp.dto.request.OpenCashDrawerRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.CashDrawerResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.CashDrawer;

public interface CashDrawerService {
    CashDrawerResponse openDrawer(OpenCashDrawerRequest request);
    CashDrawerResponse addMovement(Long drawerId, CashMovementRequest request);
    CashDrawerResponse closeDrawer(Long drawerId, CloseCashDrawerRequest request);
    CashDrawerResponse getDrawerById(Long id);
    PageAbleResponse<CashDrawer, CashDrawerResponse, Void> getDrawers(PageAbleRequest<Void> request);
}
