package com.theara.erp.service.impl;

import com.theara.erp.constant.InvoiceStatus;
import com.theara.erp.constant.KitchenStatus;
import com.theara.erp.constant.StockMovementType;
import com.theara.erp.constant.TableStatus;
import com.theara.erp.dto.request.AddOrderItemsRequest;
import com.theara.erp.dto.request.OpenTableRequest;
import com.theara.erp.dto.request.OrderLineRequest;
import com.theara.erp.dto.request.SalePaymentRequest;
import com.theara.erp.dto.request.SettleBillRequest;
import com.theara.erp.dto.response.InvoiceResponse;
import com.theara.erp.entity.*;
import com.theara.erp.exception.ApiException;
import com.theara.erp.mapper.InvoiceMapper;
import com.theara.erp.repository.*;
import com.theara.erp.service.InventoryService;
import com.theara.erp.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final int MONEY_SCALE = 4;
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final InvoiceRepository invoiceRepository;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;
    private final CustomerRepository customerRepository;
    private final WarehouseRepository warehouseRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final MenuItemRepository menuItemRepository;
    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final KitchenTicketRepository kitchenTicketRepository;
    private final InventoryService inventoryService;
    private final InvoiceMapper invoiceMapper;

    @Override
    @Transactional
    public InvoiceResponse openTable(OpenTableRequest request) {
        Company company = companyRepository.findById(request.getCompanyId()).orElseThrow(() -> ApiException.notFound("Company"));
        Branch branch = branchRepository.findById(request.getBranchId()).orElseThrow(() -> ApiException.notFound("Branch"));
        RestaurantTable table = restaurantTableRepository.findById(request.getTableId())
                .orElseThrow(() -> ApiException.notFound("RestaurantTable"));
        Customer customer = request.getCustomerId() == null ? null
                : customerRepository.findById(request.getCustomerId()).orElseThrow(() -> ApiException.notFound("Customer"));

        if (table.getStatus() == TableStatus.OCCUPIED
                || invoiceRepository.existsByTableIdAndStatus(table.getId(), InvoiceStatus.OPEN)) {
            throw ApiException.conflict("Table '" + table.getName() + "' already has an open bill");
        }

        Invoice invoice = Invoice.builder()
                .company(company)
                .branch(branch)
                .customer(customer)
                .tableId(table.getId())
                .invoiceNumber(generateInvoiceNumber(company.getId()))
                .invoiceDate(LocalDateTime.now())
                .status(InvoiceStatus.OPEN)
                .cashierId(request.getCashierId())
                .build();
        Invoice saved = invoiceRepository.save(invoice);

        table.setStatus(TableStatus.OCCUPIED);
        restaurantTableRepository.save(table);

        return invoiceMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public InvoiceResponse addItems(Long invoiceId, AddOrderItemsRequest request) {
        Invoice invoice = openBill(invoiceId);
        boolean happyHour = Boolean.TRUE.equals(request.getHappyHour());

        KitchenTicket ticket = KitchenTicket.builder()
                .invoice(invoice)
                .table(invoice.getTableId() == null ? null
                        : restaurantTableRepository.findById(invoice.getTableId()).orElse(null))
                .ticketNumber(generateTicketNumber())
                .status(KitchenStatus.NEW)
                .build();

        for (OrderLineRequest line : request.getItems()) {
            MenuItem menuItem = null;
            Product product;
            BigDecimal unitPrice;
            String description;

            if (line.getMenuItemId() != null) {
                menuItem = menuItemRepository.findById(line.getMenuItemId())
                        .orElseThrow(() -> ApiException.notFound("MenuItem"));
                product = menuItem.getProduct();
                if (product == null) {
                    throw ApiException.badRequest("Menu item '" + menuItem.getName() + "' has no linked product to bill");
                }
                unitPrice = happyHour && menuItem.getHappyHourPrice() != null
                        ? menuItem.getHappyHourPrice() : menuItem.getPrice();
                description = menuItem.getName();
            } else if (line.getProductId() != null) {
                product = productRepository.findById(line.getProductId())
                        .orElseThrow(() -> ApiException.notFound("Product"));
                unitPrice = line.getUnitPrice() != null ? line.getUnitPrice() : resolveRetailPrice(product);
                description = product.getName();
            } else {
                throw ApiException.badRequest("Each order line requires a menuItemId or a productId");
            }

            BigDecimal qty = line.getQuantity();
            BigDecimal lineBase = qty.multiply(unitPrice);
            BigDecimal lineTax = computeTax(product, lineBase);
            BigDecimal lineTotal = lineBase.add(lineTax).setScale(MONEY_SCALE, RoundingMode.HALF_UP);

            invoice.addItem(InvoiceItem.builder()
                    .product(product)
                    .description(description)
                    .quantity(qty)
                    .unitPrice(unitPrice)
                    .taxAmount(lineTax)
                    .lineTotal(lineTotal)
                    .build());

            ticket.addItem(KitchenTicketItem.builder()
                    .menuItem(menuItem)
                    .quantity(qty)
                    .note(line.getNote())
                    .status(KitchenStatus.NEW)
                    .build());
        }

        recomputeTotals(invoice);
        invoiceRepository.save(invoice);
        kitchenTicketRepository.save(ticket);
        return invoiceMapper.toResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getBill(Long invoiceId) {
        return invoiceMapper.toResponse(
                invoiceRepository.findById(invoiceId).orElseThrow(() -> ApiException.notFound("Invoice")));
    }

    @Override
    @Transactional
    public InvoiceResponse settle(Long invoiceId, SettleBillRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> ApiException.notFound("Invoice"));
        if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.VOID) {
            throw ApiException.conflict("Bill is already " + invoice.getStatus());
        }
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> ApiException.notFound("Warehouse"));

        if (request.getDiscountAmount() != null) {
            invoice.setDiscountAmount(scale(request.getDiscountAmount()));
        }
        recomputeTotals(invoice);

        BigDecimal paid = BigDecimal.ZERO;
        for (SalePaymentRequest payReq : request.getPayments()) {
            PaymentMethod method = paymentMethodRepository.findById(payReq.getPaymentMethodId())
                    .orElseThrow(() -> ApiException.notFound("PaymentMethod"));
            invoice.addPayment(Payment.builder()
                    .paymentMethod(method)
                    .amount(payReq.getAmount())
                    .referenceNo(payReq.getReferenceNo())
                    .build());
            paid = paid.add(payReq.getAmount());
        }

        BigDecimal total = invoice.getTotalAmount();
        invoice.setPaidAmount(scale(paid));
        invoice.setChangeAmount(scale(paid.subtract(total).max(BigDecimal.ZERO)));
        invoice.setStatus(paid.compareTo(total) >= 0 ? InvoiceStatus.PAID
                : paid.signum() > 0 ? InvoiceStatus.PARTIAL : InvoiceStatus.OPEN);

        for (InvoiceItem item : invoice.getItems()) {
            if (Boolean.TRUE.equals(item.getProduct().getTrackStock())) {
                inventoryService.applyMovement(
                        warehouse.getId(), item.getProduct().getId(), item.getQuantity().negate(),
                        StockMovementType.SALE, item.getProduct().getCostPrice(),
                        "INVOICE", invoice.getId(), "Dine-in " + invoice.getInvoiceNumber());
            }
        }

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            freeTable(invoice);
        }
        return invoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional
    public InvoiceResponse cancel(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> ApiException.notFound("Invoice"));
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw ApiException.conflict("A paid bill cannot be cancelled (use void)");
        }
        invoice.setStatus(InvoiceStatus.VOID);
        freeTable(invoice);
        return invoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    private Invoice openBill(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> ApiException.notFound("Invoice"));
        if (invoice.getStatus() != InvoiceStatus.OPEN) {
            throw ApiException.conflict("Items can only be added to an OPEN bill");
        }
        return invoice;
    }

    private void freeTable(Invoice invoice) {
        if (invoice.getTableId() != null) {
            restaurantTableRepository.findById(invoice.getTableId())
                    .ifPresent(t -> {
                        t.setStatus(TableStatus.AVAILABLE);
                        restaurantTableRepository.save(t);
                    });
        }
    }

    private void recomputeTotals(Invoice invoice) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal tax = BigDecimal.ZERO;
        for (InvoiceItem item : invoice.getItems()) {
            subtotal = subtotal.add(item.getQuantity().multiply(item.getUnitPrice()));
            tax = tax.add(item.getTaxAmount());
        }
        BigDecimal discount = invoice.getDiscountAmount() == null ? BigDecimal.ZERO : invoice.getDiscountAmount();
        invoice.setSubtotal(scale(subtotal));
        invoice.setTaxAmount(scale(tax));
        invoice.setTotalAmount(scale(subtotal.subtract(discount).add(tax)));
    }

    private BigDecimal computeTax(Product product, BigDecimal lineBase) {
        Tax tax = product.getTax();
        if (tax == null || tax.getRate() == null || tax.getRate().signum() == 0
                || Boolean.TRUE.equals(tax.getIsInclusive())) {
            return BigDecimal.ZERO;
        }
        return lineBase.multiply(tax.getRate()).divide(HUNDRED, MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal resolveRetailPrice(Product product) {
        return productPriceRepository.findFirstByProductIdAndPriceTypeAndIsActiveTrue(product.getId(), "RETAIL")
                .map(ProductPrice::getPrice)
                .orElseThrow(() -> ApiException.badRequest(
                        "No unitPrice provided and no active RETAIL price for product " + product.getId()));
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

    private String generateTicketNumber() {
        long seq = kitchenTicketRepository.count() + 1;
        String candidate = String.format("KOT-%06d", seq);
        while (kitchenTicketRepository.existsByTicketNumber(candidate)) {
            seq++;
            candidate = String.format("KOT-%06d", seq);
        }
        return candidate;
    }

    private BigDecimal scale(BigDecimal v) {
        return (v == null ? BigDecimal.ZERO : v).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
