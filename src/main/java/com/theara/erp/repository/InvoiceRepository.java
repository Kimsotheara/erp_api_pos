package com.theara.erp.repository;

import com.theara.erp.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    long countByCompanyId(Long companyId);
    boolean existsByCompanyIdAndInvoiceNumber(Long companyId, String invoiceNumber);
}
