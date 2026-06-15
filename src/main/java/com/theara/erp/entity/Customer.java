package com.theara.erp.entity;

import com.theara.erp.constant.Audit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SQLRestriction("is_deleted = 0")
@Entity
@Table(name = "customers")
public class Customer extends Audit {

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

    @Column(length = 30)
    private String phone;

    @Column(length = 120)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    /** Base64-encoded image (data URI or raw base64). */
    @Column(columnDefinition = "TEXT")
    private String image;

    @Column(name = "membership_no", length = 40)
    private String membershipNo;

    @Column(name = "membership_tier", length = 30)
    private String membershipTier;

    @Column(name = "loyalty_balance", nullable = false)
    private Integer loyaltyBalance = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
