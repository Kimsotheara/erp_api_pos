package com.theara.erp.dto.request;

import com.theara.erp.constant.TableStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RestaurantTableRequest {
    @NotNull(message = "branchId is required")
    private Long branchId;
    @NotBlank(message = "name is required")
    private String name;
    @Positive(message = "capacity must be positive")
    private Integer capacity;
    private TableStatus status;
}
