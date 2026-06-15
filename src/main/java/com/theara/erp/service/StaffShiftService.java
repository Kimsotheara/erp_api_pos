package com.theara.erp.service;

import com.theara.erp.dto.request.AttendancePunchRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.StaffShiftRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.StaffShiftResponse;
import com.theara.erp.entity.StaffShift;

public interface StaffShiftService {
    StaffShiftResponse rosterStaffShift(StaffShiftRequest request);
    StaffShiftResponse getStaffShiftById(Long id);
    StaffShiftResponse punch(Long id, AttendancePunchRequest request);
    StaffShiftResponse cancelStaffShift(Long id);
    PageAbleResponse<StaffShift, StaffShiftResponse, Void> getStaffShifts(PageAbleRequest<Void> request);
    void deleteStaffShift(Long id);
}
