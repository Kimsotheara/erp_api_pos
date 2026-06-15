package com.theara.erp.entity;

import com.theara.erp.constant.Audit;
import com.theara.erp.constant.ShiftSessionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SQLRestriction("is_deleted = 0")
@Entity
@Table(name = "staff_shifts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "branch_id", "shift_date", "shift_id"}))
public class StaffShift extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    @Column(name = "shift_date", nullable = false)
    private LocalDate shiftDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ShiftSessionStatus status = ShiftSessionStatus.SCHEDULED;

    @Column(name = "scheduled_start")
    private LocalDateTime scheduledStart;

    @Column(name = "scheduled_end")
    private LocalDateTime scheduledEnd;

    @Column(name = "actual_start")
    private LocalDateTime actualStart;

    @Column(name = "actual_end")
    private LocalDateTime actualEnd;

    @Column(name = "worked_minutes")
    private Integer workedMinutes;

    @Column(name = "cash_drawer_id")
    private Long cashDrawerId;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_by")
    private Long createdBy;

    @OneToMany(mappedBy = "staffShift", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ShiftAttendance> attendance = new ArrayList<>();

    public void addAttendance(ShiftAttendance event) {
        event.setStaffShift(this);
        this.attendance.add(event);
    }
}
