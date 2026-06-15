package com.theara.erp.dto.projection;

import java.math.BigDecimal;

public interface ProfitProjection {
    BigDecimal getRevenue();
    BigDecimal getCogs();
}
