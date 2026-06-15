package com.theara.erp.repository;

import com.theara.erp.entity.MedicineBatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface MedicineBatchRepository extends JpaRepository<MedicineBatch, Long> {
    boolean existsByProductIdAndBatchNumberIgnoreCase(Long productId, String batchNumber);

    /** Batches expiring on/before the cutoff and still holding stock — drives the expiry alert widget. */
    Page<MedicineBatch> findByExpiryDateLessThanEqualAndQuantityGreaterThan(
            LocalDate cutoff, java.math.BigDecimal minQuantity, Pageable pageable);
}
