package com.theara.erp.repository;

import com.theara.erp.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    Optional<ProductPrice> findFirstByProductIdAndPriceTypeAndIsActiveTrue(Long productId, String priceType);
}
