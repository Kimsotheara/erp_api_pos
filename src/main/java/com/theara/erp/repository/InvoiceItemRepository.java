package com.theara.erp.repository;

import com.theara.erp.dto.projection.ProfitProjection;
import com.theara.erp.dto.projection.TopProductProjection;
import com.theara.erp.entity.InvoiceItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    @Query("""
            select p.id as productId, p.name as productName,
                   coalesce(sum(ii.quantity), 0) as quantity,
                   coalesce(sum(ii.lineTotal), 0) as total
            from InvoiceItem ii
            join ii.product p
            join ii.invoice inv
            where inv.company.id = :companyId
              and inv.status <> com.theara.erp.constant.InvoiceStatus.VOID
              and inv.invoiceDate between :from and :to
            group by p.id, p.name
            order by coalesce(sum(ii.quantity), 0) desc
            """)
    List<TopProductProjection> topProducts(@Param("companyId") Long companyId,
                                           @Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to,
                                           Pageable pageable);

    @Query("""
            select coalesce(sum(ii.lineTotal), 0) as revenue,
                   coalesce(sum(ii.quantity * p.costPrice), 0) as cogs
            from InvoiceItem ii
            join ii.product p
            join ii.invoice inv
            where inv.company.id = :companyId
              and inv.status <> com.theara.erp.constant.InvoiceStatus.VOID
              and inv.invoiceDate between :from and :to
            """)
    ProfitProjection profit(@Param("companyId") Long companyId,
                            @Param("from") LocalDateTime from,
                            @Param("to") LocalDateTime to);
}
