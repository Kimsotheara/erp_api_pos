package com.theara.erp.mapper;

import com.theara.erp.dto.response.GoodsReceiptItemResponse;
import com.theara.erp.dto.response.GoodsReceiptResponse;
import com.theara.erp.entity.GoodsReceipt;
import com.theara.erp.entity.GoodsReceiptItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GoodsReceiptMapper {
    @Mapping(target = "purchaseOrderId", source = "purchaseOrder.id")
    @Mapping(target = "warehouseId", source = "warehouse.id")
    GoodsReceiptResponse toResponse(GoodsReceipt goodsReceipt);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    GoodsReceiptItemResponse toItemResponse(GoodsReceiptItem item);
}
