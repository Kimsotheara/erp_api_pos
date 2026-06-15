package com.theara.erp.repository;

import com.theara.erp.entity.GoodsReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsReceiptRepository extends JpaRepository<GoodsReceipt, Long> {
    long count();
    boolean existsByGrnNumber(String grnNumber);
}
