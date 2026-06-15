package com.theara.erp.service;

import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.PaymentMethodRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.PaymentMethodResponse;
import com.theara.erp.entity.PaymentMethod;

public interface PaymentMethodService {
    PaymentMethodResponse create(PaymentMethodRequest request);
    PaymentMethodResponse getById(Long id);
    PaymentMethodResponse update(Long id, PaymentMethodRequest request);
    PageAbleResponse<PaymentMethod, PaymentMethodResponse, Void> getAll(PageAbleRequest<Void> request);
    void delete(Long id);
}
