package com.theara.erp.repository;

import com.theara.erp.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    @Query(value = "select m from StockMovement m join fetch m.product join fetch m.warehouse "
            + "where (:warehouseId is null or m.warehouse.id = :warehouseId) "
            + "and (:productId is null or m.product.id = :productId)",
            countQuery = "select count(m) from StockMovement m "
                    + "where (:warehouseId is null or m.warehouse.id = :warehouseId) "
                    + "and (:productId is null or m.product.id = :productId)")
    Page<StockMovement> search(@Param("warehouseId") Long warehouseId,
                               @Param("productId") Long productId,
                               Pageable pageable);
}
