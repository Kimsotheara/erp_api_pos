package com.theara.erp.repository;

import com.theara.erp.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByCompanyIdAndCodeIgnoreCase(Long companyId, String code);
    boolean existsByCompanyIdAndCodeIgnoreCaseAndIdNot(Long companyId, String code, Long id);
}
