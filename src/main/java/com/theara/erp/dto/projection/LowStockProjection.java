package com.theara.erp.dto.projection;

import java.math.BigDecimal;

public interface LowStockProjection {
    Long getProductId();
    String getProductName();
    BigDecimal getReorderLevel();
    BigDecimal getOnHand();
}
