package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.constant.StockMovementType;
import com.theara.erp.dto.request.MedicineBatchRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.MedicineBatchResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.MedicineBatch;
import com.theara.erp.entity.Product;
import com.theara.erp.entity.Warehouse;
import com.theara.erp.mapper.MedicineBatchMapper;
import com.theara.erp.repository.*;
import com.theara.erp.service.InventoryService;
import com.theara.erp.service.MedicineBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class MedicineBatchServiceImpl implements MedicineBatchService {

    private final MedicineBatchRepository medicineBatchRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final DrugCategoryRepository drugCategoryRepository;
    private final InventoryService inventoryService;
    private final MedicineBatchMapper medicineBatchMapper;

    @Override @Transactional
    public MedicineBatchResponse createBatch(MedicineBatchRequest request) {
        if (medicineBatchRepository.existsByProductIdAndBatchNumberIgnoreCase(request.getProductId(), request.getBatchNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Batch '" + request.getBatchNumber() + "' " + ErrorCode.ALREADY_EXISTS.getDescription()
                            + " for this product");
        }
        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> notFound("Product"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId()).orElseThrow(() -> notFound("Warehouse"));
        BigDecimal cost = request.getCostPrice() != null ? request.getCostPrice() : product.getCostPrice();

        MedicineBatch batch = MedicineBatch.builder()
                .product(product)
                .warehouse(warehouse)
                .manufacturer(request.getManufacturerId() == null ? null
                        : manufacturerRepository.findById(request.getManufacturerId()).orElseThrow(() -> notFound("Manufacturer")))
                .drugCategory(request.getDrugCategoryId() == null ? null
                        : drugCategoryRepository.findById(request.getDrugCategoryId()).orElseThrow(() -> notFound("DrugCategory")))
                .batchNumber(request.getBatchNumber())
                .manufactureDate(request.getManufactureDate())
                .expiryDate(request.getExpiryDate())
                .quantity(request.getQuantity())
                .costPrice(cost)
                .build();
        MedicineBatch saved = medicineBatchRepository.save(batch);

        // Receiving a batch is the pharmacy stock-in mechanism: post it to the inventory ledger.
        inventoryService.applyMovement(warehouse.getId(), product.getId(), request.getQuantity(),
                StockMovementType.IN, cost, "BATCH", saved.getId(),
                "Medicine batch " + saved.getBatchNumber());

        return medicineBatchMapper.toResponse(saved);
    }

    @Override @Transactional(readOnly = true)
    public MedicineBatchResponse getBatchById(Long id) {
        return medicineBatchMapper.toResponse(findById(id));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<MedicineBatch, MedicineBatchResponse, Void> getBatches(PageAbleRequest<Void> request) {
        Page<MedicineBatch> page = medicineBatchRepository.findAll(request.getPageAble());
        List<MedicineBatchResponse> list = page.getContent().stream().map(medicineBatchMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<MedicineBatch, MedicineBatchResponse, Void> getExpiringBatches(int withinDays, PageAbleRequest<Void> request) {
        LocalDate cutoff = LocalDate.now().plusDays(Math.max(0, withinDays));
        Page<MedicineBatch> page = medicineBatchRepository
                .findByExpiryDateLessThanEqualAndQuantityGreaterThan(cutoff, BigDecimal.ZERO, request.getPageAble());
        List<MedicineBatchResponse> list = page.getContent().stream().map(medicineBatchMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deleteBatch(Long id) {
        medicineBatchRepository.delete(findById(id));
    }

    private MedicineBatch findById(Long id) {
        return medicineBatchRepository.findById(id).orElseThrow(() -> notFound("MedicineBatch"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
