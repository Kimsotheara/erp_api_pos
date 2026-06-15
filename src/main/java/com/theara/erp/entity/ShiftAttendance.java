package com.theara.erp.entity;

import com.theara.erp.constant.AttendanceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** Clock-in / clock-out / break punches for an assigned shift (audit trail). */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "shift_attendance")
public class ShiftAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_shift_id", nullable = false)
    private StaffShift staffShift;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 20)
    private AttendanceType eventType;

    @Column(name = "event_time", nullable = false)
    @Builder.Default
    private LocalDateTime eventTime = LocalDateTime.now();

    @Column(length = 30)
    private String source;

    @Column(length = 255)
    private String note;

    @Column(name = "created_by")
    private Long createdBy;
}
