package com.theara.erp.repository;

import com.theara.erp.dto.projection.SalesSummaryProjection;
import com.theara.erp.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    long countByCompanyId(Long companyId);
    boolean existsByCompanyIdAndInvoiceNumber(Long companyId, String invoiceNumber);

    @Query("""
            select coalesce(sum(i.totalAmount), 0) as totalSales,
                   coalesce(sum(i.taxAmount), 0)   as totalTax,
                   coalesce(sum(i.discountAmount), 0) as totalDiscount,
                   count(i) as invoiceCount
            from Invoice i
            where i.company.id = :companyId
              and i.status <> com.theara.erp.constant.InvoiceStatus.VOID
              and i.invoiceDate between :from and :to
              and (:branchId is null or i.branch.id = :branchId)
            """)
    SalesSummaryProjection salesSummary(@Param("companyId") Long companyId,
                                        @Param("branchId") Long branchId,
                                        @Param("from") LocalDateTime from,
                                        @Param("to") LocalDateTime to);
}
