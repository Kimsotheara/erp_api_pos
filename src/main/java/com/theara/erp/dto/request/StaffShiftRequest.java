package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StaffShiftRequest {
    @NotNull(message = "companyId is required")
    private Long companyId;
    @NotNull(message = "branchId is required")
    private Long branchId;
    @NotNull(message = "userId is required")
    private Long userId;
    /** Optional — null for ad-hoc shifts without a template. */
    private Long shiftId;
    @NotNull(message = "shiftDate is required")
    private LocalDate shiftDate;
    private LocalDateTime scheduledStart;
    private LocalDateTime scheduledEnd;
    private Long cashDrawerId;
    private String note;
    private Long createdBy;
}
