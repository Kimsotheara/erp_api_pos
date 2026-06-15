package com.theara.erp.service;

import com.theara.erp.constant.StockMovementType;
import com.theara.erp.entity.Stock;

import java.math.BigDecimal;

public interface InventoryService {
    Stock applyMovement(Long warehouseId, Long productId, BigDecimal signedQuantity,
                        StockMovementType type, BigDecimal unitCost,
                        String referenceType, Long referenceId, String note);

    Stock getOnHand(Long warehouseId, Long productId);
}
