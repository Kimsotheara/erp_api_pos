package com.theara.erp.dto.projection;

import java.math.BigDecimal;

public interface TopProductProjection {
    Long getProductId();
    String getProductName();
    BigDecimal getQuantity();
    BigDecimal getTotal();
}
