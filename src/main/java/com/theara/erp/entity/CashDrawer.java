package com.theara.erp.entity;

import com.theara.erp.constant.CashDrawerStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "cash_drawers")
public class CashDrawer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "opened_by")
    private Long openedBy;

    @Column(name = "closed_by")
    private Long closedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private CashDrawerStatus status = CashDrawerStatus.OPEN;

    @Column(name = "opening_balance", nullable = false, precision = 18, scale = 4)
    @Builder.Default
    private BigDecimal openingBalance = BigDecimal.ZERO;

    @Column(name = "closing_balance", precision = 18, scale = 4)
    private BigDecimal closingBalance;

    @Column(name = "expected_balance", precision = 18, scale = 4)
    private BigDecimal expectedBalance;

    @Column(precision = 18, scale = 4)
    private BigDecimal difference;

    @Column(name = "opened_at", nullable = false)
    @Builder.Default
    private LocalDateTime openedAt = LocalDateTime.now();

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @OneToMany(mappedBy = "cashDrawer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CashMovement> movements = new ArrayList<>();

    public void addMovement(CashMovement movement) {
        movement.setCashDrawer(this);
        this.movements.add(movement);
    }
}
