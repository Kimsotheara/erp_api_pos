package com.theara.erp.repository;

import com.theara.erp.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    boolean existsByBranchIdAndNameIgnoreCase(Long branchId, String name);
    boolean existsByBranchIdAndNameIgnoreCaseAndIdNot(Long branchId, String name, Long id);
}
