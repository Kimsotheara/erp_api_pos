package com.theara.erp.entity;

import com.theara.erp.constant.KitchenStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "kitchen_tickets")
public class KitchenTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private RestaurantTable table;

    @Column(name = "ticket_number", nullable = false, length = 40)
    private String ticketNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private KitchenStatus status = KitchenStatus.NEW;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "served_at")
    private LocalDateTime servedAt;

    @OneToMany(mappedBy = "kitchenTicket", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<KitchenTicketItem> items = new ArrayList<>();

    public void addItem(KitchenTicketItem item) {
        item.setKitchenTicket(this);
        this.items.add(item);
    }

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
