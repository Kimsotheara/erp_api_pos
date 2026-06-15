package com.theara.erp.dto.projection;

import java.math.BigDecimal;

public interface SalesSummaryProjection {
    BigDecimal getTotalSales();
    BigDecimal getTotalTax();
    BigDecimal getTotalDiscount();
    Long getInvoiceCount();
}
