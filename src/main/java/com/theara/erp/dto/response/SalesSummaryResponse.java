package com.theara.erp.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SalesSummaryResponse {
    private LocalDate from;
    private LocalDate to;
    private BigDecimal totalSales;
    private BigDecimal totalTax;
    private BigDecimal totalDiscount;
    private BigDecimal netSales;
    private Long invoiceCount;
    private BigDecimal averageTicket;
}
