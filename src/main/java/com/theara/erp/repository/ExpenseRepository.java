package com.theara.erp.repository;

import com.theara.erp.dto.projection.ExpenseCategoryProjection;
import com.theara.erp.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query("""
            select coalesce(e.category, 'UNCATEGORIZED') as category,
                   coalesce(sum(e.amount), 0) as total,
                   count(e) as count
            from Expense e
            where e.company.id = :companyId
              and e.expenseDate between :from and :to
            group by e.category
            order by coalesce(sum(e.amount), 0) desc
            """)
    List<ExpenseCategoryProjection> expenseByCategory(@Param("companyId") Long companyId,
                                                      @Param("from") LocalDate from,
                                                      @Param("to") LocalDate to);
}
