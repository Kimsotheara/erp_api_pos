package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
@Tag(name = "Report", description = "Analytics: sales, profit, expense, inventory and dashboard reports")
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Sales summary", description = "Totals over a date range, optionally for one branch.")
    @GetMapping("/sales-summary")
    public ResponseEntity<?> salesSummary(@RequestParam Long companyId,
                                          @RequestParam(required = false) Long branchId,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return DefaultResponse.withCode(reportService.salesSummary(companyId, branchId, from, to), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Top selling products")
    @GetMapping("/top-products")
    public ResponseEntity<?> topProducts(@RequestParam Long companyId,
                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                         @RequestParam(defaultValue = "10") int limit) {
        return DefaultResponse.withCode(reportService.topProducts(companyId, from, to, limit), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Profit report", description = "Revenue, COGS, gross profit and margin over a date range.")
    @GetMapping("/profit")
    public ResponseEntity<?> profit(@RequestParam Long companyId,
                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return DefaultResponse.withCode(reportService.profit(companyId, from, to), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Low stock alert", description = "Stock-tracked products at/below their reorder level.")
    @GetMapping("/low-stock")
    public ResponseEntity<?> lowStock(@RequestParam Long companyId) {
        return DefaultResponse.withCode(reportService.lowStock(companyId), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Expense summary by category")
    @GetMapping("/expense-summary")
    public ResponseEntity<?> expenseSummary(@RequestParam Long companyId,
                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return DefaultResponse.withCode(reportService.expenseSummary(companyId, from, to), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Dashboard snapshot", description = "Today's sales, month-to-date sales/profit, low-stock and top products.")
    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(@RequestParam Long companyId,
                                       @RequestParam(required = false) Long branchId) {
        return DefaultResponse.withCode(reportService.dashboard(companyId, branchId), ErrorCode.SUCCESS);
    }
}
