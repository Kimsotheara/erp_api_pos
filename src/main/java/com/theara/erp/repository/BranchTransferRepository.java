package com.theara.erp.repository;

import com.theara.erp.entity.BranchTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchTransferRepository extends JpaRepository<BranchTransfer, Long> {
    long count();
    boolean existsByTransferNo(String transferNo);
}
