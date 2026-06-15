package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.CurrencyRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/currencies")
@Tag(name = "Currency", description = "Currency and exchange-rate management")
@Slf4j
public class CurrencyController {
    private final CurrencyService currencyService;

    @Operation(summary = "List currencies")
    @GetMapping
    public ResponseEntity<?> getCurrencies(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(currencyService.getCurrencies(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get currency by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCurrencyById(@PathVariable Long id) {
        return DefaultResponse.withCode(currencyService.getCurrencyById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create currency")
    @PostMapping
    public ResponseEntity<?> createCurrency(@Valid @RequestBody CurrencyRequest request) {
        return DefaultResponse.withCode(currencyService.createCurrency(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update currency")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCurrency(@PathVariable Long id, @Valid @RequestBody CurrencyRequest request) {
        return DefaultResponse.withCode(currencyService.updateCurrency(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete currency", description = "Soft-deletes a currency.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCurrency(@PathVariable Long id) {
        currencyService.deleteCurrency(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
