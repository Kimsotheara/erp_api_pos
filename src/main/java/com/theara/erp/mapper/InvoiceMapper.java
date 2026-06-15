package com.theara.erp.mapper;

import com.theara.erp.dto.response.InvoiceItemResponse;
import com.theara.erp.dto.response.InvoiceResponse;
import com.theara.erp.dto.response.PaymentResponse;
import com.theara.erp.entity.Invoice;
import com.theara.erp.entity.InvoiceItem;
import com.theara.erp.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    InvoiceResponse toResponse(Invoice invoice);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    InvoiceItemResponse toItemResponse(InvoiceItem item);

    @Mapping(target = "paymentMethodId", source = "paymentMethod.id")
    @Mapping(target = "paymentMethodName", source = "paymentMethod.name")
    PaymentResponse toPaymentResponse(Payment payment);
}
