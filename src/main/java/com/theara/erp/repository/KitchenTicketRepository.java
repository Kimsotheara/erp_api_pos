package com.theara.erp.repository;

import com.theara.erp.entity.KitchenTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KitchenTicketRepository extends JpaRepository<KitchenTicket, Long> {
    long count();
    boolean existsByTicketNumber(String ticketNumber);
}
