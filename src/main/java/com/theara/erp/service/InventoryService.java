package com.theara.erp.service;

import com.theara.erp.constant.StockMovementType;
import com.theara.erp.entity.Stock;

import java.math.BigDecimal;

/**
 * Central entry point for every stock change. All callers (sales, purchasing,
 * adjustments, transfers) must go through here so the {@code stocks} on-hand
 * row and the append-only {@code stock_movements} ledger stay consistent.
 */
public interface InventoryService {

    /**
     * Apply a signed movement to a (warehouse, product) on-hand row and write a
     * ledger entry. Positive quantity = inbound, negative = outbound.
     *
     * @throws org.springframework.web.server.ResponseStatusException 409 if an
     *         outbound movement would drive the on-hand quantity negative.
     */
    Stock applyMovement(Long warehouseId, Long productId, BigDecimal signedQuantity,
                        StockMovementType type, BigDecimal unitCost,
                        String referenceType, Long referenceId, String note);

    Stock getOnHand(Long warehouseId, Long productId);
}
