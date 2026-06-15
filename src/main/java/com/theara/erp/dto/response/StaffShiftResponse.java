package com.theara.erp.dto.response;

import com.theara.erp.constant.ShiftSessionStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StaffShiftResponse {
    private Long id;
    private Long companyId;
    private Long branchId;
    private Long userId;
    private String userName;
    private Long shiftId;
    private String shiftName;
    private LocalDate shiftDate;
    private ShiftSessionStatus status;
    private LocalDateTime scheduledStart;
    private LocalDateTime scheduledEnd;
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;
    private Integer workedMinutes;
    private Long cashDrawerId;
    private String note;
    private List<ShiftAttendanceResponse> attendance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
