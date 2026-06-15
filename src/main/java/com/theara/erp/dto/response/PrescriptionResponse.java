package com.theara.erp.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PrescriptionResponse {
    private Long id;
    private Long invoiceId;
    private Long customerId;
    private String doctorName;
    private String doctorLicense;
    private String notes;
    private LocalDate issuedDate;
    private LocalDateTime createdAt;
}
