package com.theara.erp.service.impl;

import com.theara.erp.constant.CashDrawerStatus;
import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.CashMovementRequest;
import com.theara.erp.dto.request.CloseCashDrawerRequest;
import com.theara.erp.dto.request.OpenCashDrawerRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.CashDrawerResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Branch;
import com.theara.erp.entity.CashDrawer;
import com.theara.erp.entity.CashMovement;
import com.theara.erp.mapper.CashDrawerMapper;
import com.theara.erp.repository.BranchRepository;
import com.theara.erp.repository.CashDrawerRepository;
import com.theara.erp.service.CashDrawerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class CashDrawerServiceImpl implements CashDrawerService {
    private final CashDrawerRepository cashDrawerRepository;
    private final BranchRepository branchRepository;
    private final CashDrawerMapper cashDrawerMapper;

    @Override @Transactional
    public CashDrawerResponse openDrawer(OpenCashDrawerRequest request) {
        Branch branch = branchRepository.findById(request.getBranchId()).orElseThrow(() -> notFound("Branch"));
        cashDrawerRepository.findFirstByBranchIdAndStatusOrderByOpenedAtDesc(branch.getId(), CashDrawerStatus.OPEN)
                .ifPresent(d -> { throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Branch already has an OPEN cash drawer (id " + d.getId() + ")"); });

        CashDrawer drawer = CashDrawer.builder()
                .branch(branch)
                .openedBy(request.getOpenedBy())
                .status(CashDrawerStatus.OPEN)
                .openingBalance(request.getOpeningBalance() != null ? request.getOpeningBalance() : BigDecimal.ZERO)
                .openedAt(LocalDateTime.now())
                .build();
        return cashDrawerMapper.toResponse(cashDrawerRepository.save(drawer));
    }

    @Override @Transactional
    public CashDrawerResponse addMovement(Long drawerId, CashMovementRequest request) {
        CashDrawer drawer = openDrawerOrThrow(drawerId);
        drawer.addMovement(CashMovement.builder()
                .direction(request.getDirection())
                .amount(request.getAmount())
                .reason(request.getReason())
                .referenceType(request.getReferenceType())
                .referenceId(request.getReferenceId())
                .createdBy(request.getCreatedBy())
                .build());
        return cashDrawerMapper.toResponse(cashDrawerRepository.save(drawer));
    }

    @Override @Transactional
    public CashDrawerResponse closeDrawer(Long drawerId, CloseCashDrawerRequest request) {
        CashDrawer drawer = openDrawerOrThrow(drawerId);
        BigDecimal expected = computeExpected(drawer);
        drawer.setStatus(CashDrawerStatus.CLOSED);
        drawer.setClosedBy(request.getClosedBy());
        drawer.setClosingBalance(request.getCountedBalance());
        drawer.setExpectedBalance(expected);
        drawer.setDifference(request.getCountedBalance().subtract(expected));
        drawer.setClosedAt(LocalDateTime.now());
        return cashDrawerMapper.toResponse(cashDrawerRepository.save(drawer));
    }

    @Override @Transactional(readOnly = true)
    public CashDrawerResponse getDrawerById(Long id) {
        return cashDrawerMapper.toResponse(cashDrawerRepository.findById(id).orElseThrow(() -> notFound("CashDrawer")));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<CashDrawer, CashDrawerResponse, Void> getDrawers(PageAbleRequest<Void> request) {
        Page<CashDrawer> page = cashDrawerRepository.findAll(request.getPageAble());
        List<CashDrawerResponse> list = page.getContent().stream().map(cashDrawerMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    private BigDecimal computeExpected(CashDrawer drawer) {
        BigDecimal total = drawer.getOpeningBalance();
        for (CashMovement m : drawer.getMovements()) {
            total = "OUT".equalsIgnoreCase(m.getDirection())
                    ? total.subtract(m.getAmount())
                    : total.add(m.getAmount());
        }
        return total;
    }

    private CashDrawer openDrawerOrThrow(Long drawerId) {
        CashDrawer drawer = cashDrawerRepository.findById(drawerId).orElseThrow(() -> notFound("CashDrawer"));
        if (drawer.getStatus() != CashDrawerStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cash drawer is already CLOSED");
        }
        return drawer;
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
