package com.theara.erp.dto.request;

import com.theara.erp.constant.TableStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TableStatusRequest {
    @NotNull(message = "status is required")
    private TableStatus status;
}
