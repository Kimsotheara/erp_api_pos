package com.theara.erp.repository;

import com.theara.erp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByCompanyIdAndSkuIgnoreCase(Long companyId, String sku);
    boolean existsByCompanyIdAndSkuIgnoreCaseAndIdNot(Long companyId, String sku, Long id);
}
