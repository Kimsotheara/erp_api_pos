package com.theara.erp.repository;

import com.theara.erp.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    boolean existsByCompanyIdAndCodeIgnoreCase(Long companyId, String code);
    boolean existsByCompanyIdAndCodeIgnoreCaseAndIdNot(Long companyId, String code, Long id);
}
