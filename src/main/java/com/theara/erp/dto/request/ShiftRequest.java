package com.theara.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ShiftRequest {
    @NotNull(message = "branchId is required")
    private Long branchId;
    @NotBlank(message = "name is required")
    private String name;
    @NotNull(message = "startTime is required")
    private LocalTime startTime;
    @NotNull(message = "endTime is required")
    private LocalTime endTime;
    private Boolean crossesMidnight;
    @PositiveOrZero(message = "breakMinutes must be zero or positive")
    private Integer breakMinutes;
    private Boolean isActive;
}
