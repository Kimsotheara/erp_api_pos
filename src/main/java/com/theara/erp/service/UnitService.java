package com.theara.erp.service;

import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.UnitRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.UnitResponse;
import com.theara.erp.entity.Unit;

public interface UnitService {
    UnitResponse createUnit(UnitRequest request);
    UnitResponse getUnitById(Long id);
    UnitResponse updateUnit(Long id, UnitRequest request);
    PageAbleResponse<Unit, UnitResponse, Void> getUnits(PageAbleRequest<Void> request);
    void deleteUnit(Long id);
}
