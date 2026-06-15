package com.theara.erp.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardResponse {
    private BigDecimal todaySales;
    private Long todayInvoiceCount;
    private BigDecimal monthSales;
    private Long monthInvoiceCount;
    private BigDecimal monthGrossProfit;
    private Long lowStockCount;
    private List<TopProductResponse> topProducts;
    private List<LowStockResponse> lowStockItems;
}
