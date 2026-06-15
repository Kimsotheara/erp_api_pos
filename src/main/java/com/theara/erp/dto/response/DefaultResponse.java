package com.theara.erp.dto.response;

import com.theara.erp.constant.ErrorCode;
import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class DefaultResponse {

    public <T> ResponseEntity<NormalizeResponse<T>> OK(T body) {
        return withCode(body, ErrorCode.SUCCESS);
    }

    public <T> ResponseEntity<NormalizeResponse<T>> withCode(T body, ErrorCode errorCode) {
        NormalizeResponse<T> response = new NormalizeResponse<>(body);
        response.setResultCode(errorCode.getCode());
        response.setResult(errorCode.isResult());
        response.setResultMessage(errorCode.getDescription());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    public <T> ResponseEntity<NormalizeResponse<T>> withMessage(T body, ErrorCode errorCode, String message) {
        NormalizeResponse<T> response = new NormalizeResponse<>(body);
        response.setResultCode(errorCode.getCode());
        response.setResult(errorCode.isResult());
        response.setResultMessage(message);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }
}
