package com.theara.erp.service.impl;

import com.theara.erp.common.PageMapper;
import com.theara.erp.dto.request.CurrencyRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.CurrencyResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Currency;
import com.theara.erp.exception.ApiException;
import com.theara.erp.mapper.CurrencyMapper;
import com.theara.erp.repository.CurrencyRepository;
import com.theara.erp.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j @Service @RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    @Override @Transactional
    public CurrencyResponse createCurrency(CurrencyRequest request) {
        if (currencyRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw ApiException.codeExists("Currency code '" + request.getCode() + "'");
        }
        Currency currency = new Currency();
        apply(currency, request);
        return currencyMapper.toResponse(currencyRepository.save(currency));
    }

    @Override @Transactional(readOnly = true)
    public CurrencyResponse getCurrencyById(Long id) {
        return currencyMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public CurrencyResponse updateCurrency(Long id, CurrencyRequest request) {
        Currency currency = findById(id);
        apply(currency, request);
        return currencyMapper.toResponse(currencyRepository.save(currency));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<Currency, CurrencyResponse, Void> getCurrencies(PageAbleRequest<Void> request) {
        return PageMapper.toResponse(currencyRepository.findAll(request.getPageAble()), currencyMapper::toResponse);
    }

    @Override @Transactional
    public void deleteCurrency(Long id) {
        Currency currency = findById(id);
        currency.setIsDeleted(1);
        currencyRepository.save(currency);
    }

    private void apply(Currency c, CurrencyRequest r) {
        c.setCode(r.getCode().toUpperCase());
        c.setName(r.getName());
        c.setSymbol(r.getSymbol());
        c.setExchangeRate(r.getExchangeRate() != null ? r.getExchangeRate() : BigDecimal.ONE);
        if (r.getIsBase() != null) c.setIsBase(r.getIsBase());
        if (r.getIsActive() != null) c.setIsActive(r.getIsActive());
    }

    private Currency findById(Long id) {
        return currencyRepository.findById(id).orElseThrow(() -> ApiException.notFound("Currency"));
    }
}
