package com.theara.erp.repository;

import com.theara.erp.entity.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByWarehouseIdAndProductId(Long warehouseId, Long productId);

    // Fetch the product so callers can read it after the session closes (open-in-view=false).
    @Query("select s from Stock s join fetch s.product where s.warehouse.id = :warehouseId and s.product.id = :productId")
    Optional<Stock> findOnHandWithProduct(@Param("warehouseId") Long warehouseId,
                                          @Param("productId") Long productId);

    // Pessimistic lock to keep concurrent sales from oversubscribing the same row.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.warehouse.id = :warehouseId and s.product.id = :productId")
    Optional<Stock> lockByWarehouseIdAndProductId(@Param("warehouseId") Long warehouseId,
                                                  @Param("productId") Long productId);
}
