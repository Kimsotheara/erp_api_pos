package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.WarehouseRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.WarehouseResponse;
import com.theara.erp.entity.Branch;
import com.theara.erp.entity.Warehouse;
import com.theara.erp.mapper.WarehouseMapper;
import com.theara.erp.repository.BranchRepository;
import com.theara.erp.repository.WarehouseRepository;
import com.theara.erp.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final BranchRepository branchRepository;
    private final WarehouseMapper warehouseMapper;

    @Override @Transactional
    public WarehouseResponse create(WarehouseRequest request) {
        if (warehouseRepository.existsByBranchIdAndCodeIgnoreCase(request.getBranchId(), request.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Warehouse code '" + request.getCode() + "' " + ErrorCode.CODE_ALREADY_EXISTS.getDescription());
        }
        Warehouse w = new Warehouse();
        apply(w, request);
        return warehouseMapper.toResponse(warehouseRepository.save(w));
    }

    @Override @Transactional(readOnly = true)
    public WarehouseResponse getById(Long id) {
        return warehouseMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public WarehouseResponse update(Long id, WarehouseRequest request) {
        Warehouse w = findById(id);
        apply(w, request);
        return warehouseMapper.toResponse(warehouseRepository.save(w));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<Warehouse, WarehouseResponse, Void> getAll(PageAbleRequest<Void> request) {
        Page<Warehouse> page = warehouseRepository.findAll(request.getPageAble());
        List<WarehouseResponse> list = page.getContent().stream().map(warehouseMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void delete(Long id) {
        Warehouse w = findById(id);
        w.setIsDeleted(1);
        warehouseRepository.save(w);
    }

    private void apply(Warehouse w, WarehouseRequest r) {
        Branch branch = branchRepository.findById(r.getBranchId())
                .orElseThrow(() -> notFound("Branch"));
        w.setBranch(branch);
        w.setCode(r.getCode());
        w.setName(r.getName());
        if (r.getIsDefault() != null) w.setIsDefault(r.getIsDefault());
        if (r.getIsActive() != null) w.setIsActive(r.getIsActive());
    }

    private Warehouse findById(Long id) {
        return warehouseRepository.findById(id).orElseThrow(() -> notFound("Warehouse"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
