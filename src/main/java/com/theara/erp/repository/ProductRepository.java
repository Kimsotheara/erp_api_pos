package com.theara.erp.repository;

import com.theara.erp.dto.projection.LowStockProjection;
import com.theara.erp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByCompanyIdAndSkuIgnoreCase(Long companyId, String sku);
    boolean existsByCompanyIdAndSkuIgnoreCaseAndIdNot(Long companyId, String sku, Long id);

    /** Stock-tracked products whose total on-hand across warehouses is at/below their reorder level. */
    @Query("""
            select p.id as productId, p.name as productName,
                   p.reorderLevel as reorderLevel,
                   coalesce(sum(s.quantity), 0) as onHand
            from Product p
            left join Stock s on s.product = p
            where p.company.id = :companyId
              and p.trackStock = true
            group by p.id, p.name, p.reorderLevel
            having coalesce(sum(s.quantity), 0) <= p.reorderLevel
            """)
    List<LowStockProjection> lowStock(@Param("companyId") Long companyId);
}
