package com.theara.erp.dto.request;

import lombok.*;

import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PrescriptionRequest {
    private Long invoiceId;
    private Long customerId;
    private String doctorName;
    private String doctorLicense;
    private String notes;
    private LocalDate issuedDate;
}
