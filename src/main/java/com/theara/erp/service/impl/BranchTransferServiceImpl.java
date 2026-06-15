package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.constant.StockMovementType;
import com.theara.erp.constant.TransferStatus;
import com.theara.erp.dto.request.BranchTransferItemRequest;
import com.theara.erp.dto.request.BranchTransferRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.BranchTransferResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.*;
import com.theara.erp.mapper.BranchTransferMapper;
import com.theara.erp.repository.*;
import com.theara.erp.service.BranchTransferService;
import com.theara.erp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class BranchTransferServiceImpl implements BranchTransferService {
    private final BranchTransferRepository branchTransferRepository;
    private final BranchRepository branchRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final BranchTransferMapper branchTransferMapper;

    @Override @Transactional
    public BranchTransferResponse createTransfer(BranchTransferRequest request) {
        if (request.getFromBranchId().equals(request.getToBranchId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source and destination branch must differ");
        }
        Branch fromBranch = branchRepository.findById(request.getFromBranchId()).orElseThrow(() -> notFound("From branch"));
        Branch toBranch = branchRepository.findById(request.getToBranchId()).orElseThrow(() -> notFound("To branch"));
        Warehouse fromWarehouse = warehouseRepository.findById(request.getFromWarehouseId()).orElseThrow(() -> notFound("From warehouse"));
        Warehouse toWarehouse = warehouseRepository.findById(request.getToWarehouseId()).orElseThrow(() -> notFound("To warehouse"));

        BranchTransfer transfer = BranchTransfer.builder()
                .transferNo(generateTransferNo())
                .fromBranch(fromBranch)
                .toBranch(toBranch)
                .fromWarehouse(fromWarehouse)
                .toWarehouse(toWarehouse)
                .status(TransferStatus.DRAFT)
                .transferDate(request.getTransferDate() != null ? request.getTransferDate() : LocalDate.now())
                .note(request.getNote())
                .createdBy(request.getCreatedBy())
                .build();

        for (BranchTransferItemRequest r : request.getItems()) {
            Product product = productRepository.findById(r.getProductId())
                    .orElseThrow(() -> notFound("Product " + r.getProductId()));
            transfer.addItem(BranchTransferItem.builder().product(product).quantity(r.getQuantity()).build());
        }
        return branchTransferMapper.toResponse(branchTransferRepository.save(transfer));
    }

    @Override @Transactional(readOnly = true)
    public BranchTransferResponse getTransferById(Long id) {
        return branchTransferMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public BranchTransferResponse shipTransfer(Long id) {
        BranchTransfer transfer = findById(id);
        if (transfer.getStatus() != TransferStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only DRAFT transfers can be shipped");
        }
        for (BranchTransferItem item : transfer.getItems()) {
            inventoryService.applyMovement(
                    transfer.getFromWarehouse().getId(), item.getProduct().getId(), item.getQuantity().negate(),
                    StockMovementType.TRANSFER, item.getProduct().getCostPrice(),
                    "TRANSFER", transfer.getId(), "Transfer out " + transfer.getTransferNo());
        }
        transfer.setStatus(TransferStatus.IN_TRANSIT);
        return branchTransferMapper.toResponse(branchTransferRepository.save(transfer));
    }

    @Override @Transactional
    public BranchTransferResponse receiveTransfer(Long id) {
        BranchTransfer transfer = findById(id);
        if (transfer.getStatus() != TransferStatus.IN_TRANSIT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only IN_TRANSIT transfers can be received");
        }
        for (BranchTransferItem item : transfer.getItems()) {
            inventoryService.applyMovement(
                    transfer.getToWarehouse().getId(), item.getProduct().getId(), item.getQuantity(),
                    StockMovementType.TRANSFER, item.getProduct().getCostPrice(),
                    "TRANSFER", transfer.getId(), "Transfer in " + transfer.getTransferNo());
        }
        transfer.setStatus(TransferStatus.RECEIVED);
        transfer.setReceivedDate(LocalDate.now());
        return branchTransferMapper.toResponse(branchTransferRepository.save(transfer));
    }

    @Override @Transactional
    public BranchTransferResponse cancelTransfer(Long id) {
        BranchTransfer transfer = findById(id);
        switch (transfer.getStatus()) {
            case DRAFT -> {  }
            case IN_TRANSIT -> {
                for (BranchTransferItem item : transfer.getItems()) {
                    inventoryService.applyMovement(
                            transfer.getFromWarehouse().getId(), item.getProduct().getId(), item.getQuantity(),
                            StockMovementType.TRANSFER, item.getProduct().getCostPrice(),
                            "TRANSFER", transfer.getId(), "Transfer cancelled " + transfer.getTransferNo());
                }
            }
            default -> throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Received or cancelled transfers cannot be cancelled");
        }
        transfer.setStatus(TransferStatus.CANCELLED);
        return branchTransferMapper.toResponse(branchTransferRepository.save(transfer));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<BranchTransfer, BranchTransferResponse, Void> getTransfers(PageAbleRequest<Void> request) {
        Page<BranchTransfer> page = branchTransferRepository.findAll(request.getPageAble());
        List<BranchTransferResponse> list = page.getContent().stream().map(branchTransferMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deleteTransfer(Long id) {
        BranchTransfer transfer = findById(id);
        if (transfer.getStatus() != TransferStatus.DRAFT && transfer.getStatus() != TransferStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Only DRAFT or CANCELLED transfers can be deleted");
        }
        transfer.setIsDeleted(1);
        branchTransferRepository.save(transfer);
    }

    private String generateTransferNo() {
        long seq = branchTransferRepository.count() + 1;
        String candidate = String.format("TRF-%06d", seq);
        while (branchTransferRepository.existsByTransferNo(candidate)) {
            seq++;
            candidate = String.format("TRF-%06d", seq);
        }
        return candidate;
    }

    private BranchTransfer findById(Long id) {
        return branchTransferRepository.findById(id).orElseThrow(() -> notFound("BranchTransfer"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
