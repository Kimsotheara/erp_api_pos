package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.constant.StockMovementType;
import com.theara.erp.entity.Product;
import com.theara.erp.entity.Stock;
import com.theara.erp.entity.StockMovement;
import com.theara.erp.entity.Warehouse;
import com.theara.erp.repository.ProductRepository;
import com.theara.erp.repository.StockMovementRepository;
import com.theara.erp.repository.StockRepository;
import com.theara.erp.repository.WarehouseRepository;
import com.theara.erp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Stock applyMovement(Long warehouseId, Long productId, BigDecimal signedQuantity,
                               StockMovementType type, BigDecimal unitCost,
                               String referenceType, Long referenceId, String note) {

        if (signedQuantity == null || signedQuantity.signum() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movement quantity must be non-zero");
        }
        BigDecimal cost = unitCost != null ? unitCost : BigDecimal.ZERO;

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> notFound("Warehouse"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> notFound("Product"));

        // Lock (or create) the on-hand row so concurrent sales can't oversell.
        Stock stock = stockRepository.lockByWarehouseIdAndProductId(warehouseId, productId)
                .orElseGet(() -> Stock.builder()
                        .warehouse(warehouse)
                        .product(product)
                        .quantity(BigDecimal.ZERO)
                        .avgCost(BigDecimal.ZERO)
                        .build());

        // Ensure associations are the already-initialized instances (safe to read after the session closes).
        stock.setWarehouse(warehouse);
        stock.setProduct(product);

        BigDecimal newQty = stock.getQuantity().add(signedQuantity);
        if (newQty.signum() < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    ErrorCode.INSUFFICIENT_STOCK.getDescription()
                            + " (product " + productId + ": on-hand " + stock.getQuantity()
                            + ", requested " + signedQuantity.abs() + ")");
        }

        // Moving-average cost, updated only on inbound movements.
        if (signedQuantity.signum() > 0) {
            BigDecimal oldValue = stock.getQuantity().multiply(stock.getAvgCost());
            BigDecimal inValue = signedQuantity.multiply(cost);
            stock.setAvgCost(newQty.signum() == 0 ? cost
                    : oldValue.add(inValue).divide(newQty, 4, java.math.RoundingMode.HALF_UP));
        }
        stock.setQuantity(newQty);
        stockRepository.save(stock);

        stockMovementRepository.save(StockMovement.builder()
                .warehouse(warehouse)
                .product(product)
                .movementType(type)
                .quantity(signedQuantity)
                .unitCost(cost)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .note(note)
                .build());

        return stock;
    }

    @Override
    @Transactional(readOnly = true)
    public Stock getOnHand(Long warehouseId, Long productId) {
        return stockRepository.findOnHandWithProduct(warehouseId, productId)
                .orElseThrow(() -> notFound("Stock"));
    }

    private ResponseStatusException notFound(String entity) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, entity + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
