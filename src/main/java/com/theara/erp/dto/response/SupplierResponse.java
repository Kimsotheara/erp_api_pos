package com.theara.erp.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SupplierResponse implements Imageable {
    private Long id;
    private Long companyId;
    private String code;
    private String name;
    private String taxNumber;
    private String phone;
    private String email;
    private String address;
    private String image;
    private BigDecimal outstandingBalance;
    private Boolean isActive;
    private List<SupplierContactResponse> contacts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
