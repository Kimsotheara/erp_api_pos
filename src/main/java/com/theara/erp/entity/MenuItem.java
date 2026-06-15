package com.theara.erp.entity;

import com.theara.erp.constant.Audit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SQLRestriction("is_deleted = 0")
@Entity
@Table(name = "menu_items")
public class MenuItem extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String image;

    @Column(length = 80)
    private String category;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal price;

    @Column(name = "happy_hour_price", precision = 18, scale = 4)
    private BigDecimal happyHourPrice;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;
}
