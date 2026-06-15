package com.theara.erp.mapper;

import com.theara.erp.dto.response.PurchaseOrderItemResponse;
import com.theara.erp.dto.response.PurchaseOrderResponse;
import com.theara.erp.entity.PurchaseOrder;
import com.theara.erp.entity.PurchaseOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper {

    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    PurchaseOrderResponse toResponse(PurchaseOrder purchaseOrder);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    PurchaseOrderItemResponse toItemResponse(PurchaseOrderItem item);
}
