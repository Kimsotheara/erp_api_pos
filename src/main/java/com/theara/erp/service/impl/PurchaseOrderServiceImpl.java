package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.constant.PoStatus;
import com.theara.erp.dto.request.ApprovePurchaseOrderRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.PurchaseOrderItemRequest;
import com.theara.erp.dto.request.PurchaseOrderRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.PurchaseOrderResponse;
import com.theara.erp.entity.*;
import com.theara.erp.mapper.PurchaseOrderMapper;
import com.theara.erp.repository.*;
import com.theara.erp.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    private static final int MONEY_SCALE = 4;

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;

    @Override @Transactional
    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request) {
        Company company = companyRepository.findById(request.getCompanyId()).orElseThrow(() -> notFound("Company"));
        Branch branch = branchRepository.findById(request.getBranchId()).orElseThrow(() -> notFound("Branch"));
        Supplier supplier = supplierRepository.findById(request.getSupplierId()).orElseThrow(() -> notFound("Supplier"));

        PurchaseOrder po = PurchaseOrder.builder()
                .company(company)
                .branch(branch)
                .supplier(supplier)
                .poNumber(generatePoNumber(company.getId()))
                .status(PoStatus.DRAFT)
                .orderDate(request.getOrderDate() != null ? request.getOrderDate() : LocalDate.now())
                .expectedDate(request.getExpectedDate())
                .note(request.getNote())
                .requestedBy(request.getRequestedBy())
                .build();

        buildItems(po, request.getItems());
        recalc(po);
        return purchaseOrderMapper.toResponse(purchaseOrderRepository.save(po));
    }

    @Override @Transactional(readOnly = true)
    public PurchaseOrderResponse getPurchaseOrderById(Long id) {
        return purchaseOrderMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public PurchaseOrderResponse updatePurchaseOrder(Long id, PurchaseOrderRequest request) {
        PurchaseOrder po = findById(id);
        if (po.getStatus() != PoStatus.DRAFT && po.getStatus() != PoStatus.REQUESTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Only DRAFT or REQUESTED purchase orders can be edited");
        }
        po.setBranch(branchRepository.findById(request.getBranchId()).orElseThrow(() -> notFound("Branch")));
        po.setSupplier(supplierRepository.findById(request.getSupplierId()).orElseThrow(() -> notFound("Supplier")));
        if (request.getOrderDate() != null) po.setOrderDate(request.getOrderDate());
        po.setExpectedDate(request.getExpectedDate());
        po.setNote(request.getNote());
        po.getItems().clear();
        buildItems(po, request.getItems());
        recalc(po);
        return purchaseOrderMapper.toResponse(purchaseOrderRepository.save(po));
    }

    @Override @Transactional
    public PurchaseOrderResponse submitPurchaseOrder(Long id) {
        PurchaseOrder po = findById(id);
        if (po.getStatus() != PoStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only DRAFT purchase orders can be submitted");
        }
        po.setStatus(PoStatus.REQUESTED);
        return purchaseOrderMapper.toResponse(purchaseOrderRepository.save(po));
    }

    @Override @Transactional
    public PurchaseOrderResponse approvePurchaseOrder(Long id, ApprovePurchaseOrderRequest request) {
        PurchaseOrder po = findById(id);
        if (po.getStatus() != PoStatus.REQUESTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only REQUESTED purchase orders can be approved/rejected");
        }
        boolean approve = request.getApprove() == null || request.getApprove();
        po.setStatus(approve ? PoStatus.APPROVED : PoStatus.REJECTED);
        po.setApprovedBy(request.getApprovedBy());
        po.setApprovedAt(LocalDateTime.now());
        if (request.getNote() != null) po.setNote(request.getNote());
        return purchaseOrderMapper.toResponse(purchaseOrderRepository.save(po));
    }

    @Override @Transactional
    public PurchaseOrderResponse cancelPurchaseOrder(Long id) {
        PurchaseOrder po = findById(id);
        if (EnumSet.of(PoStatus.RECEIVED, PoStatus.CANCELLED).contains(po.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Received or already-cancelled purchase orders cannot be cancelled");
        }
        po.setStatus(PoStatus.CANCELLED);
        return purchaseOrderMapper.toResponse(purchaseOrderRepository.save(po));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<PurchaseOrder, PurchaseOrderResponse, Void> getPurchaseOrders(PageAbleRequest<Void> request) {
        Page<PurchaseOrder> page = purchaseOrderRepository.findAll(request.getPageAble());
        List<PurchaseOrderResponse> list = page.getContent().stream().map(purchaseOrderMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deletePurchaseOrder(Long id) {
        PurchaseOrder po = findById(id);
        po.setIsDeleted(1);
        purchaseOrderRepository.save(po);
    }

    private void buildItems(PurchaseOrder po, List<PurchaseOrderItemRequest> itemRequests) {
        for (PurchaseOrderItemRequest r : itemRequests) {
            Product product = productRepository.findById(r.getProductId())
                    .orElseThrow(() -> notFound("Product " + r.getProductId()));
            BigDecimal lineTotal = r.getQuantity().multiply(r.getUnitCost()).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
            po.addItem(PurchaseOrderItem.builder()
                    .product(product)
                    .quantity(r.getQuantity())
                    .unitCost(r.getUnitCost())
                    .receivedQty(BigDecimal.ZERO)
                    .lineTotal(lineTotal)
                    .build());
        }
    }

    private void recalc(PurchaseOrder po) {
        BigDecimal subtotal = po.getItems().stream()
                .map(PurchaseOrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        po.setSubtotal(subtotal.setScale(MONEY_SCALE, RoundingMode.HALF_UP));
        po.setTaxAmount(BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP));
        po.setTotalAmount(subtotal.setScale(MONEY_SCALE, RoundingMode.HALF_UP));
    }

    private String generatePoNumber(Long companyId) {
        long seq = purchaseOrderRepository.countByCompanyId(companyId) + 1;
        String candidate = String.format("PO-%d-%06d", companyId, seq);
        while (purchaseOrderRepository.existsByCompanyIdAndPoNumber(companyId, candidate)) {
            seq++;
            candidate = String.format("PO-%d-%06d", companyId, seq);
        }
        return candidate;
    }

    private PurchaseOrder findById(Long id) {
        return purchaseOrderRepository.findById(id).orElseThrow(() -> notFound("PurchaseOrder"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
