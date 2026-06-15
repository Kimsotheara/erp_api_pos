package com.theara.erp.dto.response;

import com.theara.erp.constant.AttendanceType;
import lombok.*;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ShiftAttendanceResponse {
    private Long id;
    private AttendanceType eventType;
    private LocalDateTime eventTime;
    private String source;
    private String note;
    private Long createdBy;
}
