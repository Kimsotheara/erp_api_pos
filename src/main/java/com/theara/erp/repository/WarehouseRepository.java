package com.theara.erp.repository;

import com.theara.erp.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    boolean existsByBranchIdAndCodeIgnoreCase(Long branchId, String code);
}
