package com.theara.erp.dto.projection;

import java.math.BigDecimal;

public interface ExpenseCategoryProjection {
    String getCategory();
    BigDecimal getTotal();
    Long getCount();
}
