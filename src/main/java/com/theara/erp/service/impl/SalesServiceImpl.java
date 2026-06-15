package com.theara.erp.service.impl;

import com.theara.erp.common.PageMapper;
import com.theara.erp.constant.ErrorCode;
import com.theara.erp.constant.InvoiceStatus;
import com.theara.erp.constant.StockMovementType;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.SaleItemRequest;
import com.theara.erp.dto.request.SalePaymentRequest;
import com.theara.erp.dto.request.SaleRequest;
import com.theara.erp.dto.request.VoidInvoiceRequest;
import com.theara.erp.dto.response.InvoiceResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.*;
import com.theara.erp.mapper.InvoiceMapper;
import com.theara.erp.repository.*;
import com.theara.erp.service.InventoryService;
import com.theara.erp.service.SalesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {
    private static final int MONEY_SCALE = 4;
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final InvoiceRepository invoiceRepository;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;
    private final CustomerRepository customerRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final MedicineBatchRepository medicineBatchRepository;
    private final InventoryService inventoryService;
    private final InvoiceMapper invoiceMapper;

    @Override
    @Transactional
    public InvoiceResponse checkout(SaleRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> notFound("Company"));
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> notFound("Branch"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> notFound("Warehouse"));
        Customer customer = request.getCustomerId() == null ? null
                : customerRepository.findById(request.getCustomerId()).orElseThrow(() -> notFound("Customer"));

        Invoice invoice = Invoice.builder()
                .company(company)
                .branch(branch)
                .customer(customer)
                .invoiceNumber(generateInvoiceNumber(company.getId()))
                .invoiceDate(java.time.LocalDateTime.now())
                .status(InvoiceStatus.OPEN)
                .cashierId(request.getCashierId())
                .note(request.getNote())
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (SaleItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> notFound("Product " + itemReq.getProductId()));

            BigDecimal qty = itemReq.getQuantity();
            BigDecimal unitPrice = resolveUnitPrice(itemReq, product);
            BigDecimal discount = nz(itemReq.getDiscountAmount());

            BigDecimal lineBase = qty.multiply(unitPrice).subtract(discount);
            if (lineBase.signum() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Discount exceeds line amount for product " + product.getId());
            }
            BigDecimal lineTax = computeTax(product, lineBase);
            BigDecimal lineTotal = lineBase.add(lineTax).setScale(MONEY_SCALE, RoundingMode.HALF_UP);

            InvoiceItem item = InvoiceItem.builder()
                    .product(product)
                    .description(product.getName())
                    .quantity(qty)
                    .unitPrice(unitPrice)
                    .discountAmount(discount.setScale(MONEY_SCALE, RoundingMode.HALF_UP))
                    .taxAmount(lineTax)
                    .lineTotal(lineTotal)
                    .build();
            invoice.addItem(item);

            subtotal = subtotal.add(qty.multiply(unitPrice));
            totalDiscount = totalDiscount.add(discount);
            totalTax = totalTax.add(lineTax);
        }

        BigDecimal invoiceDiscount = nz(request.getDiscountAmount());
        totalDiscount = totalDiscount.add(invoiceDiscount);
        BigDecimal total = subtotal.subtract(totalDiscount).add(totalTax);
        if (total.signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invoice discount exceeds total");
        }

        BigDecimal paid = BigDecimal.ZERO;
        for (SalePaymentRequest payReq : request.getPayments()) {
            PaymentMethod method = paymentMethodRepository.findById(payReq.getPaymentMethodId())
                    .orElseThrow(() -> notFound("PaymentMethod"));
            invoice.addPayment(Payment.builder()
                    .paymentMethod(method)
                    .amount(payReq.getAmount())
                    .referenceNo(payReq.getReferenceNo())
                    .build());
            paid = paid.add(payReq.getAmount());
        }

        invoice.setSubtotal(scale(subtotal));
        invoice.setDiscountAmount(scale(totalDiscount));
        invoice.setTaxAmount(scale(totalTax));
        invoice.setTotalAmount(scale(total));
        invoice.setPaidAmount(scale(paid));
        invoice.setChangeAmount(scale(paid.subtract(total).max(BigDecimal.ZERO)));
        invoice.setStatus(resolveStatus(paid, total));

        Invoice saved = invoiceRepository.save(invoice);

        for (InvoiceItem item : saved.getItems()) {
            if (Boolean.TRUE.equals(item.getProduct().getTrackStock())) {
                inventoryService.applyMovement(
                        warehouse.getId(),
                        item.getProduct().getId(),
                        item.getQuantity().negate(),
                        StockMovementType.SALE,
                        item.getProduct().getCostPrice(),
                        "INVOICE",
                        saved.getId(),
                        "Sale " + saved.getInvoiceNumber());
                allocateBatchesFefo(warehouse.getId(), item);
            }
        }

        return invoiceMapper.toResponse(saved);
    }

    private void allocateBatchesFefo(Long warehouseId, InvoiceItem item) {
        List<MedicineBatch> batches = medicineBatchRepository
                .findByProductIdAndWarehouseIdAndQuantityGreaterThanOrderByExpiryDateAsc(
                        item.getProduct().getId(), warehouseId, BigDecimal.ZERO);
        if (batches.isEmpty()) {
            return;
        }
        BigDecimal remaining = item.getQuantity();
        Long firstBatchId = null;
        for (MedicineBatch batch : batches) {
            if (remaining.signum() <= 0) break;
            BigDecimal take = batch.getQuantity().min(remaining);
            batch.setQuantity(batch.getQuantity().subtract(take));
            if (firstBatchId == null) firstBatchId = batch.getId();
            remaining = remaining.subtract(take);
        }
        if (remaining.signum() > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    ErrorCode.INSUFFICIENT_STOCK.getDescription()
                            + " (medicine batch stock for product " + item.getProduct().getId() + ")");
        }
        item.setBatchId(firstBatchId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageAbleResponse<Invoice, InvoiceResponse, Void> getInvoices(PageAbleRequest<Void> request) {
        return PageMapper.toResponse(invoiceRepository.findAll(request.getPageAble()), invoiceMapper::toResponse);
    }

    @Override
    @Transactional
    public InvoiceResponse voidInvoice(Long id, VoidInvoiceRequest request) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> notFound("Invoice"));
        if (invoice.getStatus() == InvoiceStatus.VOID) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invoice is already voided");
        }
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> notFound("Warehouse"));

        for (InvoiceItem item : invoice.getItems()) {
            if (Boolean.TRUE.equals(item.getProduct().getTrackStock())) {
                inventoryService.applyMovement(
                        warehouse.getId(),
                        item.getProduct().getId(),
                        item.getQuantity(),
                        StockMovementType.RETURN,
                        item.getProduct().getCostPrice(),
                        "INVOICE_VOID",
                        invoice.getId(),
                        "Void " + invoice.getInvoiceNumber());
                if (item.getBatchId() != null) {
                    medicineBatchRepository.findById(item.getBatchId())
                            .ifPresent(b -> b.setQuantity(b.getQuantity().add(item.getQuantity())));
                }
            }
        }
        invoice.setStatus(InvoiceStatus.VOID);
        if (request.getReason() != null) {
            invoice.setNote(request.getReason());
        }
        return invoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> notFound("Invoice"));
        return invoiceMapper.toResponse(invoice);
    }

    private BigDecimal resolveUnitPrice(SaleItemRequest itemReq, Product product) {
        if (itemReq.getUnitPrice() != null) {
            return itemReq.getUnitPrice();
        }
        return productPriceRepository
                .findFirstByProductIdAndPriceTypeAndIsActiveTrue(product.getId(), "RETAIL")
                .map(ProductPrice::getPrice)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No unit price provided and no active RETAIL price for product " + product.getId()));
    }

    private BigDecimal computeTax(Product product, BigDecimal lineBase) {
        Tax tax = product.getTax();
        if (tax == null || tax.getRate() == null || tax.getRate().signum() == 0
                || Boolean.TRUE.equals(tax.getIsInclusive())) {
            return BigDecimal.ZERO;
        }
        return lineBase.multiply(tax.getRate()).divide(HUNDRED, MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private InvoiceStatus resolveStatus(BigDecimal paid, BigDecimal total) {
        if (paid.compareTo(total) >= 0) return InvoiceStatus.PAID;
        if (paid.signum() > 0) return InvoiceStatus.PARTIAL;
        return InvoiceStatus.OPEN;
    }

    private String generateInvoiceNumber(Long companyId) {
        long seq = invoiceRepository.countByCompanyId(companyId) + 1;
        String candidate = String.format("INV-%d-%06d", companyId, seq);
        while (invoiceRepository.existsByCompanyIdAndInvoiceNumber(companyId, candidate)) {
            seq++;
            candidate = String.format("INV-%d-%06d", companyId, seq);
        }
        return candidate;
    }

    private BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private BigDecimal scale(BigDecimal v) {
        return v.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private ResponseStatusException notFound(String entity) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, entity + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
