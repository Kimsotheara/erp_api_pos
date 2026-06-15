package com.theara.erp.dto.response;

import com.theara.erp.constant.TableStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RestaurantTableResponse {
    private Long id;
    private Long branchId;
    private String name;
    private Integer capacity;
    private TableStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
