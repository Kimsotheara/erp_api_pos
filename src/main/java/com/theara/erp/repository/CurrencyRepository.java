package com.theara.erp.repository;

import com.theara.erp.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    boolean existsByCodeIgnoreCase(String code);
}
