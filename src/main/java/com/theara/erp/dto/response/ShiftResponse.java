package com.theara.erp.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ShiftResponse {
    private Long id;
    private Long branchId;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean crossesMidnight;
    private Integer breakMinutes;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
