package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.ShiftRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.ShiftResponse;
import com.theara.erp.entity.Branch;
import com.theara.erp.entity.Shift;
import com.theara.erp.mapper.ShiftMapper;
import com.theara.erp.repository.BranchRepository;
import com.theara.erp.repository.ShiftRepository;
import com.theara.erp.service.ShiftService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final BranchRepository branchRepository;
    private final ShiftMapper shiftMapper;

    @Override @Transactional
    public ShiftResponse createShift(ShiftRequest request) {
        if (shiftRepository.existsByBranchIdAndNameIgnoreCase(request.getBranchId(), request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Shift '" + request.getName() + "' " + ErrorCode.ALREADY_EXISTS.getDescription());
        }
        Shift shift = new Shift();
        apply(shift, request);
        return shiftMapper.toResponse(shiftRepository.save(shift));
    }

    @Override @Transactional(readOnly = true)
    public ShiftResponse getShiftById(Long id) {
        return shiftMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public ShiftResponse updateShift(Long id, ShiftRequest request) {
        Shift shift = findById(id);
        if (shiftRepository.existsByBranchIdAndNameIgnoreCaseAndIdNot(request.getBranchId(), request.getName(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Shift '" + request.getName() + "' " + ErrorCode.ALREADY_EXISTS.getDescription());
        }
        apply(shift, request);
        return shiftMapper.toResponse(shiftRepository.save(shift));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<Shift, ShiftResponse, Void> getShifts(PageAbleRequest<Void> request) {
        Page<Shift> page = shiftRepository.findAll(request.getPageAble());
        List<ShiftResponse> list = page.getContent().stream().map(shiftMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deleteShift(Long id) {
        Shift shift = findById(id);
        shift.setIsDeleted(1);
        shiftRepository.save(shift);
    }

    private void apply(Shift s, ShiftRequest r) {
        Branch branch = branchRepository.findById(r.getBranchId()).orElseThrow(() -> notFound("Branch"));
        s.setBranch(branch);
        s.setName(r.getName());
        s.setStartTime(r.getStartTime());
        s.setEndTime(r.getEndTime());
        if (r.getCrossesMidnight() != null) s.setCrossesMidnight(r.getCrossesMidnight());
        if (r.getBreakMinutes() != null) s.setBreakMinutes(r.getBreakMinutes());
        if (r.getIsActive() != null) s.setIsActive(r.getIsActive());
    }

    private Shift findById(Long id) {
        return shiftRepository.findById(id).orElseThrow(() -> notFound("Shift"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
