package com.theara.erp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Standardized response wrapper for all API endpoints")
public class NormalizeResponse<T> {
    public NormalizeResponse(T body) {
        this.body = body;
    }

    @Schema(description = "Indicates if the operation was successful", example = "true")
    private boolean result = true;

    @Schema(description = "Result code (0000 = success, others = error codes)", example = "0000")
    private String resultCode = "0000";

    @Schema(description = "Human-readable result message", example = "Success")
    private String resultMessage = "Success";

    @Schema(description = "Response data payload")
    private T body;
}
