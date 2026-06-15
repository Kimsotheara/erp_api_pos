package com.theara.erp.service;

import com.theara.erp.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    SalesSummaryResponse salesSummary(Long companyId, Long branchId, LocalDate from, LocalDate to);
    List<TopProductResponse> topProducts(Long companyId, LocalDate from, LocalDate to, int limit);
    ProfitResponse profit(Long companyId, LocalDate from, LocalDate to);
    List<LowStockResponse> lowStock(Long companyId);
    List<ExpenseCategoryResponse> expenseSummary(Long companyId, LocalDate from, LocalDate to);
    DashboardResponse dashboard(Long companyId, Long branchId);
}
