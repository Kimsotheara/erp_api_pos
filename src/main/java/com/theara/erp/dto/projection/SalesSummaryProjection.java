package com.theara.erp.dto.projection;

import java.math.BigDecimal;

/** Aggregate row for a sales summary over a date range. */
public interface SalesSummaryProjection {
    BigDecimal getTotalSales();
    BigDecimal getTotalTax();
    BigDecimal getTotalDiscount();
    Long getInvoiceCount();
}
