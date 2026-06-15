package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.constant.PoStatus;
import com.theara.erp.constant.StockMovementType;
import com.theara.erp.dto.request.GoodsReceiptItemRequest;
import com.theara.erp.dto.request.GoodsReceiptRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.GoodsReceiptResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.*;
import com.theara.erp.mapper.GoodsReceiptMapper;
import com.theara.erp.repository.*;
import com.theara.erp.service.GoodsReceiptService;
import com.theara.erp.service.InventoryService;
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
public class GoodsReceiptServiceImpl implements GoodsReceiptService {

    private final GoodsReceiptRepository goodsReceiptRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final GoodsReceiptMapper goodsReceiptMapper;

    @Override @Transactional
    public GoodsReceiptResponse receiveGoods(GoodsReceiptRequest request) {
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> notFound("Warehouse"));

        PurchaseOrder po = null;
        if (request.getPurchaseOrderId() != null) {
            po = purchaseOrderRepository.findById(request.getPurchaseOrderId())
                    .orElseThrow(() -> notFound("PurchaseOrder"));
            if (po.getStatus() != PoStatus.APPROVED && po.getStatus() != PoStatus.PARTIAL) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Goods can only be received against an APPROVED or PARTIAL purchase order");
            }
        }

        GoodsReceipt grn = GoodsReceipt.builder()
                .purchaseOrder(po)
                .warehouse(warehouse)
                .grnNumber(generateGrnNumber())
                .receivedDate(request.getReceivedDate() != null ? request.getReceivedDate() : LocalDate.now())
                .receivedBy(request.getReceivedBy())
                .note(request.getNote())
                .build();

        for (GoodsReceiptItemRequest r : request.getItems()) {
            Product product = productRepository.findById(r.getProductId())
                    .orElseThrow(() -> notFound("Product " + r.getProductId()));
            BigDecimal unitCost = resolveUnitCost(r, po, product);
            grn.addItem(GoodsReceiptItem.builder()
                    .product(product)
                    .quantity(r.getQuantity())
                    .unitCost(unitCost)
                    .build());

            if (po != null) {
                updatePoReceivedQty(po, product.getId(), r.getQuantity());
            }
            // Keep the product's standard cost in step with the latest purchase price.
            if (unitCost.signum() > 0) {
                product.setCostPrice(unitCost);
                productRepository.save(product);
            }
        }

        GoodsReceipt saved = goodsReceiptRepository.save(grn);

        // Post stock-in movements once the GRN has an id so the ledger can reference it.
        for (GoodsReceiptItem item : saved.getItems()) {
            inventoryService.applyMovement(
                    warehouse.getId(),
                    item.getProduct().getId(),
                    item.getQuantity(),
                    StockMovementType.PURCHASE,
                    item.getUnitCost(),
                    "GRN",
                    saved.getId(),
                    "Goods receipt " + saved.getGrnNumber());
        }

        if (po != null) {
            po.setStatus(isFullyReceived(po) ? PoStatus.RECEIVED : PoStatus.PARTIAL);
            purchaseOrderRepository.save(po);
        }

        return goodsReceiptMapper.toResponse(saved);
    }

    @Override @Transactional(readOnly = true)
    public GoodsReceiptResponse getGoodsReceiptById(Long id) {
        return goodsReceiptMapper.toResponse(
                goodsReceiptRepository.findById(id).orElseThrow(() -> notFound("GoodsReceipt")));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<GoodsReceipt, GoodsReceiptResponse, Void> getGoodsReceipts(PageAbleRequest<Void> request) {
        Page<GoodsReceipt> page = goodsReceiptRepository.findAll(request.getPageAble());
        List<GoodsReceiptResponse> list = page.getContent().stream().map(goodsReceiptMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    private BigDecimal resolveUnitCost(GoodsReceiptItemRequest r, PurchaseOrder po, Product product) {
        if (r.getUnitCost() != null) return r.getUnitCost();
        if (po != null) {
            return po.getItems().stream()
                    .filter(i -> i.getProduct().getId().equals(product.getId()))
                    .map(PurchaseOrderItem::getUnitCost)
                    .findFirst()
                    .orElse(product.getCostPrice());
        }
        return product.getCostPrice();
    }

    private void updatePoReceivedQty(PurchaseOrder po, Long productId, BigDecimal qty) {
        po.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(i -> i.setReceivedQty(i.getReceivedQty().add(qty)));
    }

    private boolean isFullyReceived(PurchaseOrder po) {
        return po.getItems().stream().allMatch(i -> i.getReceivedQty().compareTo(i.getQuantity()) >= 0);
    }

    private String generateGrnNumber() {
        long seq = goodsReceiptRepository.count() + 1;
        String candidate = String.format("GRN-%06d", seq);
        while (goodsReceiptRepository.existsByGrnNumber(candidate)) {
            seq++;
            candidate = String.format("GRN-%06d", seq);
        }
        return candidate;
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
