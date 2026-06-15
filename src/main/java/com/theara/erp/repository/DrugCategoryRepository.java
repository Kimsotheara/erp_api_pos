package com.theara.erp.repository;

import com.theara.erp.entity.DrugCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrugCategoryRepository extends JpaRepository<DrugCategory, Long> {
}
