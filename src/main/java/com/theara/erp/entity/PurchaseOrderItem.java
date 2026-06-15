package com.theara.erp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "purchase_order_items")
public class PurchaseOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal quantity;

    @Column(name = "unit_cost", nullable = false, precision = 18, scale = 4)
    private BigDecimal unitCost;

    @Column(name = "received_qty", nullable = false, precision = 18, scale = 3)
    @Builder.Default
    private BigDecimal receivedQty = BigDecimal.ZERO;

    @Column(name = "line_total", nullable = false, precision = 18, scale = 4)
    @Builder.Default
    private BigDecimal lineTotal = BigDecimal.ZERO;
}
