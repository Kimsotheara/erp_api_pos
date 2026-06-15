package com.theara.erp.mapper;

import com.theara.erp.dto.response.ShiftAttendanceResponse;
import com.theara.erp.dto.response.StaffShiftResponse;
import com.theara.erp.entity.ShiftAttendance;
import com.theara.erp.entity.StaffShift;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StaffShiftMapper {

    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.fullName")
    @Mapping(target = "shiftId", source = "shift.id")
    @Mapping(target = "shiftName", source = "shift.name")
    StaffShiftResponse toResponse(StaffShift staffShift);

    ShiftAttendanceResponse toAttendanceResponse(ShiftAttendance attendance);
}
