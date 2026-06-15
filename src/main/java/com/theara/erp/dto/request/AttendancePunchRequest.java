package com.theara.erp.dto.request;

import com.theara.erp.constant.AttendanceType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttendancePunchRequest {
    @NotNull(message = "eventType is required")
    private AttendanceType eventType;
    private String source;
    private String note;
    private Long createdBy;
}
