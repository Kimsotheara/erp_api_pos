package com.theara.erp.entity;

import com.theara.erp.constant.Audit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SQLRestriction("is_deleted = 0")
@Entity
@Table(name = "income")
public class Income extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Column(length = 80)
    private String category;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal amount;

    @Column(length = 255)
    private String description;

    @Column(name = "income_date", nullable = false)
    @Builder.Default
    private LocalDate incomeDate = LocalDate.now();

    @Column(name = "created_by")
    private Long createdBy;
}
