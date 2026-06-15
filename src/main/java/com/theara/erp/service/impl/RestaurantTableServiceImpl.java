package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.constant.TableStatus;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.RestaurantTableRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.RestaurantTableResponse;
import com.theara.erp.entity.Branch;
import com.theara.erp.entity.RestaurantTable;
import com.theara.erp.mapper.RestaurantTableMapper;
import com.theara.erp.repository.BranchRepository;
import com.theara.erp.repository.RestaurantTableRepository;
import com.theara.erp.service.RestaurantTableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class RestaurantTableServiceImpl implements RestaurantTableService {
    private final RestaurantTableRepository restaurantTableRepository;
    private final BranchRepository branchRepository;
    private final RestaurantTableMapper restaurantTableMapper;

    @Override @Transactional
    public RestaurantTableResponse createTable(RestaurantTableRequest request) {
        if (restaurantTableRepository.existsByBranchIdAndNameIgnoreCase(request.getBranchId(), request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Table '" + request.getName() + "' " + ErrorCode.ALREADY_EXISTS.getDescription());
        }
        RestaurantTable table = new RestaurantTable();
        apply(table, request);
        return restaurantTableMapper.toResponse(restaurantTableRepository.save(table));
    }

    @Override @Transactional(readOnly = true)
    public RestaurantTableResponse getTableById(Long id) {
        return restaurantTableMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public RestaurantTableResponse updateTable(Long id, RestaurantTableRequest request) {
        RestaurantTable table = findById(id);
        if (restaurantTableRepository.existsByBranchIdAndNameIgnoreCaseAndIdNot(request.getBranchId(), request.getName(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Table '" + request.getName() + "' " + ErrorCode.ALREADY_EXISTS.getDescription());
        }
        apply(table, request);
        return restaurantTableMapper.toResponse(restaurantTableRepository.save(table));
    }

    @Override @Transactional
    public RestaurantTableResponse updateStatus(Long id, TableStatus status) {
        RestaurantTable table = findById(id);
        table.setStatus(status);
        return restaurantTableMapper.toResponse(restaurantTableRepository.save(table));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<RestaurantTable, RestaurantTableResponse, Void> getTables(PageAbleRequest<Void> request) {
        Page<RestaurantTable> page = restaurantTableRepository.findAll(request.getPageAble());
        List<RestaurantTableResponse> list = page.getContent().stream().map(restaurantTableMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deleteTable(Long id) {
        RestaurantTable table = findById(id);
        table.setIsDeleted(1);
        restaurantTableRepository.save(table);
    }

    private void apply(RestaurantTable t, RestaurantTableRequest r) {
        Branch branch = branchRepository.findById(r.getBranchId()).orElseThrow(() -> notFound("Branch"));
        t.setBranch(branch);
        t.setName(r.getName());
        if (r.getCapacity() != null) t.setCapacity(r.getCapacity());
        if (r.getStatus() != null) t.setStatus(r.getStatus());
    }

    private RestaurantTable findById(Long id) {
        return restaurantTableRepository.findById(id).orElseThrow(() -> notFound("RestaurantTable"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
