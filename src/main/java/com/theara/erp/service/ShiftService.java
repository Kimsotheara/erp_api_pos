package com.theara.erp.service;

import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.ShiftRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.ShiftResponse;
import com.theara.erp.entity.Shift;

public interface ShiftService {
    ShiftResponse createShift(ShiftRequest request);
    ShiftResponse getShiftById(Long id);
    ShiftResponse updateShift(Long id, ShiftRequest request);
    PageAbleResponse<Shift, ShiftResponse, Void> getShifts(PageAbleRequest<Void> request);
    void deleteShift(Long id);
}
