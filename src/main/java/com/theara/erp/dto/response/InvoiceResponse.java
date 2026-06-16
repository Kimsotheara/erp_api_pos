package com.theara.erp.dto.response;

import com.theara.erp.constant.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private Long id;
    private Long companyId;
    private Long branchId;
    private Long customerId;
    private String customerName;
    private String invoiceNumber;
    private InvoiceStatus status;
    private LocalDateTime invoiceDate;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal changeAmount;
    private BigDecimal refundedAmount;
    private String note;
    private Long tableId;
    private List<InvoiceItemResponse> items;
    private List<PaymentResponse> payments;
}
