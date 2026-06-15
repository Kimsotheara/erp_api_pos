package com.theara.erp.entity;

import com.theara.erp.constant.Audit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SQLRestriction("is_deleted = 0")
@Entity
@Table(name = "suppliers")
public class Supplier extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(length = 30)
    private String code;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(name = "tax_number", length = 50)
    private String taxNumber;

    @Column(length = 30)
    private String phone;

    @Column(length = 120)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(columnDefinition = "TEXT")
    private String image;

    @Column(name = "outstanding_balance", nullable = false, precision = 18, scale = 4)
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplierContact> contacts = new ArrayList<>();

    public void addContact(SupplierContact contact) {
        contact.setSupplier(this);
        this.contacts.add(contact);
    }
}
