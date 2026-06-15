package com.theara.erp.service.impl;

import com.theara.erp.dto.projection.ExpenseCategoryProjection;
import com.theara.erp.dto.projection.LowStockProjection;
import com.theara.erp.dto.projection.ProfitProjection;
import com.theara.erp.dto.projection.TopProductProjection;
import com.theara.erp.dto.projection.SalesSummaryProjection;
import com.theara.erp.dto.response.*;
import com.theara.erp.repository.ExpenseRepository;
import com.theara.erp.repository.InvoiceItemRepository;
import com.theara.erp.repository.InvoiceRepository;
import com.theara.erp.repository.ProductRepository;
import com.theara.erp.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final int MONEY_SCALE = 4;

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final ProductRepository productRepository;
    private final ExpenseRepository expenseRepository;

    @Override @Transactional(readOnly = true)
    public SalesSummaryResponse salesSummary(Long companyId, Long branchId, LocalDate from, LocalDate to) {
        SalesSummaryProjection p = invoiceRepository.salesSummary(companyId, branchId, startOf(from), endOf(to));
        return buildSummary(from, to, p);
    }

    @Override @Transactional(readOnly = true)
    public List<TopProductResponse> topProducts(Long companyId, LocalDate from, LocalDate to, int limit) {
        return invoiceItemRepository.topProducts(companyId, startOf(from), endOf(to),
                        PageRequest.of(0, Math.max(1, limit)))
                .stream()
                .map(this::toTopProduct)
                .toList();
    }

    @Override @Transactional(readOnly = true)
    public ProfitResponse profit(Long companyId, LocalDate from, LocalDate to) {
        ProfitProjection p = invoiceItemRepository.profit(companyId, startOf(from), endOf(to));
        BigDecimal revenue = nz(p.getRevenue());
        BigDecimal cogs = nz(p.getCogs());
        BigDecimal gross = revenue.subtract(cogs);
        BigDecimal margin = revenue.signum() == 0 ? BigDecimal.ZERO
                : gross.multiply(BigDecimal.valueOf(100)).divide(revenue, 2, RoundingMode.HALF_UP);
        return ProfitResponse.builder()
                .from(from).to(to)
                .revenue(scale(revenue)).cogs(scale(cogs)).grossProfit(scale(gross))
                .marginPercent(margin)
                .build();
    }

    @Override @Transactional(readOnly = true)
    public List<LowStockResponse> lowStock(Long companyId) {
        return productRepository.lowStock(companyId).stream().map(this::toLowStock).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<ExpenseCategoryResponse> expenseSummary(Long companyId, LocalDate from, LocalDate to) {
        return expenseRepository.expenseByCategory(companyId, from, to).stream().map(this::toExpenseCategory).toList();
    }

    @Override @Transactional(readOnly = true)
    public DashboardResponse dashboard(Long companyId, Long branchId) {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        SalesSummaryProjection todayP = invoiceRepository.salesSummary(companyId, branchId, startOf(today), endOf(today));
        SalesSummaryProjection monthP = invoiceRepository.salesSummary(companyId, branchId, startOf(monthStart), endOf(today));
        ProfitProjection monthProfit = invoiceItemRepository.profit(companyId, startOf(monthStart), endOf(today));
        List<LowStockResponse> lowStock = lowStock(companyId);
        List<TopProductResponse> top = topProducts(companyId, monthStart, today, 5);

        return DashboardResponse.builder()
                .todaySales(scale(nz(todayP.getTotalSales())))
                .todayInvoiceCount(todayP.getInvoiceCount())
                .monthSales(scale(nz(monthP.getTotalSales())))
                .monthInvoiceCount(monthP.getInvoiceCount())
                .monthGrossProfit(scale(nz(monthProfit.getRevenue()).subtract(nz(monthProfit.getCogs()))))
                .lowStockCount((long) lowStock.size())
                .topProducts(top)
                .lowStockItems(lowStock)
                .build();
    }

    private SalesSummaryResponse buildSummary(LocalDate from, LocalDate to, SalesSummaryProjection p) {
        BigDecimal totalSales = nz(p.getTotalSales());
        BigDecimal totalTax = nz(p.getTotalTax());
        BigDecimal totalDiscount = nz(p.getTotalDiscount());
        long count = p.getInvoiceCount() != null ? p.getInvoiceCount() : 0L;
        BigDecimal avg = count == 0 ? BigDecimal.ZERO
                : totalSales.divide(BigDecimal.valueOf(count), MONEY_SCALE, RoundingMode.HALF_UP);
        return SalesSummaryResponse.builder()
                .from(from).to(to)
                .totalSales(scale(totalSales))
                .totalTax(scale(totalTax))
                .totalDiscount(scale(totalDiscount))
                .netSales(scale(totalSales.subtract(totalTax)))
                .invoiceCount(count)
                .averageTicket(avg)
                .build();
    }

    private TopProductResponse toTopProduct(TopProductProjection p) {
        return TopProductResponse.builder()
                .productId(p.getProductId()).productName(p.getProductName())
                .quantity(p.getQuantity()).total(scale(nz(p.getTotal())))
                .build();
    }

    private LowStockResponse toLowStock(LowStockProjection p) {
        return LowStockResponse.builder()
                .productId(p.getProductId()).productName(p.getProductName())
                .reorderLevel(p.getReorderLevel()).onHand(p.getOnHand())
                .build();
    }

    private ExpenseCategoryResponse toExpenseCategory(ExpenseCategoryProjection p) {
        return ExpenseCategoryResponse.builder()
                .category(p.getCategory()).total(scale(nz(p.getTotal()))).count(p.getCount())
                .build();
    }

    private LocalDateTime startOf(LocalDate d) {
        return d.atStartOfDay();
    }

    private LocalDateTime endOf(LocalDate d) {
        return d.atTime(LocalTime.MAX);
    }

    private BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private BigDecimal scale(BigDecimal v) {
        return nz(v).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
