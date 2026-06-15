package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.GoodsReceiptRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.GoodsReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goods-receipts")
@Tag(name = "Goods Receipt", description = "Procurement: receive goods into inventory (GRN)")
@Slf4j
public class GoodsReceiptController {
    private final GoodsReceiptService goodsReceiptService;

    @Operation(summary = "List goods receipts")
    @GetMapping
    public ResponseEntity<?> getGoodsReceipts(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(goodsReceiptService.getGoodsReceipts(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get goods receipt by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getGoodsReceiptById(@PathVariable Long id) {
        return DefaultResponse.withCode(goodsReceiptService.getGoodsReceiptById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Receive goods",
            description = "Posts a goods receipt (optionally against a PO) and increases inventory.")
    @PostMapping
    public ResponseEntity<?> receiveGoods(@Valid @RequestBody GoodsReceiptRequest request) {
        return DefaultResponse.withCode(goodsReceiptService.receiveGoods(request), ErrorCode.CREATED);
    }
}
