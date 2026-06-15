package com.theara.erp.mapper;

import com.theara.erp.dto.response.BranchTransferItemResponse;
import com.theara.erp.dto.response.BranchTransferResponse;
import com.theara.erp.entity.BranchTransfer;
import com.theara.erp.entity.BranchTransferItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchTransferMapper {
    @Mapping(target = "fromBranchId", source = "fromBranch.id")
    @Mapping(target = "toBranchId", source = "toBranch.id")
    @Mapping(target = "fromWarehouseId", source = "fromWarehouse.id")
    @Mapping(target = "toWarehouseId", source = "toWarehouse.id")
    BranchTransferResponse toResponse(BranchTransfer transfer);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    BranchTransferItemResponse toItemResponse(BranchTransferItem item);
}
