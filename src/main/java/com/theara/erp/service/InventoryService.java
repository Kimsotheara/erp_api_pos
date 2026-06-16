package com.theara.erp.service;

import com.theara.erp.constant.StockMovementType;
import com.theara.erp.entity.Stock;
import com.theara.erp.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface InventoryService {
    Stock applyMovement(Long warehouseId, Long productId, BigDecimal signedQuantity,
                        StockMovementType type, BigDecimal unitCost,
                        String referenceType, Long referenceId, String note);

    Stock getOnHand(Long warehouseId, Long productId);

    Stock stockOut(Long warehouseId, Long productId, BigDecimal quantity, String note);

    Stock adjust(Long warehouseId, Long productId, BigDecimal countedQuantity, String note);

    Page<StockMovement> getMovements(Long warehouseId, Long productId, Pageable pageable);
}
