package com.theara.erp.repository;

import com.theara.erp.entity.StaffShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffShiftRepository extends JpaRepository<StaffShift, Long> {
}
