package com.theara.erp.service.impl;

import com.theara.erp.constant.AttendanceType;
import com.theara.erp.constant.ErrorCode;
import com.theara.erp.constant.ShiftSessionStatus;
import com.theara.erp.dto.request.AttendancePunchRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.StaffShiftRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.StaffShiftResponse;
import com.theara.erp.entity.*;
import com.theara.erp.mapper.StaffShiftMapper;
import com.theara.erp.repository.*;
import com.theara.erp.service.StaffShiftService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class StaffShiftServiceImpl implements StaffShiftService {
    private final StaffShiftRepository staffShiftRepository;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;
    private final StaffShiftMapper staffShiftMapper;

    @Override @Transactional
    public StaffShiftResponse rosterStaffShift(StaffShiftRequest request) {
        Company company = companyRepository.findById(request.getCompanyId()).orElseThrow(() -> notFound("Company"));
        Branch branch = branchRepository.findById(request.getBranchId()).orElseThrow(() -> notFound("Branch"));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> notFound("User"));
        Shift shift = request.getShiftId() == null ? null
                : shiftRepository.findById(request.getShiftId()).orElseThrow(() -> notFound("Shift"));

        StaffShift staffShift = StaffShift.builder()
                .company(company)
                .branch(branch)
                .user(user)
                .shift(shift)
                .shiftDate(request.getShiftDate())
                .status(ShiftSessionStatus.SCHEDULED)
                .scheduledStart(request.getScheduledStart())
                .scheduledEnd(request.getScheduledEnd())
                .cashDrawerId(request.getCashDrawerId())
                .note(request.getNote())
                .createdBy(request.getCreatedBy())
                .build();
        return staffShiftMapper.toResponse(staffShiftRepository.save(staffShift));
    }

    @Override @Transactional(readOnly = true)
    public StaffShiftResponse getStaffShiftById(Long id) {
        return staffShiftMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public StaffShiftResponse punch(Long id, AttendancePunchRequest request) {
        StaffShift staffShift = findById(id);
        if (staffShift.getStatus() == ShiftSessionStatus.CLOSED
                || staffShift.getStatus() == ShiftSessionStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot punch a " + staffShift.getStatus() + " shift");
        }
        LocalDateTime now = LocalDateTime.now();
        staffShift.addAttendance(ShiftAttendance.builder()
                .eventType(request.getEventType())
                .eventTime(now)
                .source(request.getSource())
                .note(request.getNote())
                .createdBy(request.getCreatedBy())
                .build());

        switch (request.getEventType()) {
            case CLOCK_IN -> {
                if (staffShift.getActualStart() == null) staffShift.setActualStart(now);
                staffShift.setStatus(ShiftSessionStatus.OPEN);
            }
            case CLOCK_OUT -> {
                staffShift.setActualEnd(now);
                staffShift.setStatus(ShiftSessionStatus.CLOSED);
                staffShift.setWorkedMinutes(computeWorkedMinutes(staffShift));
            }
            default -> {  }
        }
        return staffShiftMapper.toResponse(staffShiftRepository.save(staffShift));
    }

    @Override @Transactional
    public StaffShiftResponse cancelStaffShift(Long id) {
        StaffShift staffShift = findById(id);
        if (staffShift.getStatus() == ShiftSessionStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Closed shifts cannot be cancelled");
        }
        staffShift.setStatus(ShiftSessionStatus.CANCELLED);
        return staffShiftMapper.toResponse(staffShiftRepository.save(staffShift));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<StaffShift, StaffShiftResponse, Void> getStaffShifts(PageAbleRequest<Void> request) {
        Page<StaffShift> page = staffShiftRepository.findAll(request.getPageAble());
        List<StaffShiftResponse> list = page.getContent().stream().map(staffShiftMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deleteStaffShift(Long id) {
        StaffShift staffShift = findById(id);
        staffShift.setIsDeleted(1);
        staffShiftRepository.save(staffShift);
    }

    private int computeWorkedMinutes(StaffShift staffShift) {
        if (staffShift.getActualStart() == null || staffShift.getActualEnd() == null) return 0;
        long total = Duration.between(staffShift.getActualStart(), staffShift.getActualEnd()).toMinutes();

        List<ShiftAttendance> events = staffShift.getAttendance().stream()
                .sorted(Comparator.comparing(ShiftAttendance::getEventTime))
                .toList();
        long breakMinutes = 0;
        LocalDateTime breakStart = null;
        for (ShiftAttendance e : events) {
            if (e.getEventType() == AttendanceType.BREAK_START) {
                breakStart = e.getEventTime();
            } else if (e.getEventType() == AttendanceType.BREAK_END && breakStart != null) {
                breakMinutes += Duration.between(breakStart, e.getEventTime()).toMinutes();
                breakStart = null;
            }
        }
        return (int) Math.max(0, total - breakMinutes);
    }

    private StaffShift findById(Long id) {
        return staffShiftRepository.findById(id).orElseThrow(() -> notFound("StaffShift"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
