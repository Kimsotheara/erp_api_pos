package com.theara.erp.service;

import com.theara.erp.dto.request.CurrencyRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.CurrencyResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Currency;

public interface CurrencyService {
    CurrencyResponse createCurrency(CurrencyRequest request);
    CurrencyResponse getCurrencyById(Long id);
    CurrencyResponse updateCurrency(Long id, CurrencyRequest request);
    PageAbleResponse<Currency, CurrencyResponse, Void> getCurrencies(PageAbleRequest<Void> request);
    void deleteCurrency(Long id);
}
