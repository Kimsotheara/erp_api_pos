package com.theara.erp.repository;

import com.theara.erp.constant.CashDrawerStatus;
import com.theara.erp.entity.CashDrawer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CashDrawerRepository extends JpaRepository<CashDrawer, Long> {
    Optional<CashDrawer> findFirstByBranchIdAndStatusOrderByOpenedAtDesc(Long branchId, CashDrawerStatus status);
}
