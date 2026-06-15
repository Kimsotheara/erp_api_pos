package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.CurrencyRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.CurrencyResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Currency;
import com.theara.erp.mapper.CurrencyMapper;
import com.theara.erp.repository.CurrencyRepository;
import com.theara.erp.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    @Override @Transactional
    public CurrencyResponse createCurrency(CurrencyRequest request) {
        if (currencyRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Currency code '" + request.getCode() + "' " + ErrorCode.CODE_ALREADY_EXISTS.getDescription());
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
        Page<Currency> page = currencyRepository.findAll(request.getPageAble());
        List<CurrencyResponse> list = page.getContent().stream().map(currencyMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
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
        return currencyRepository.findById(id).orElseThrow(() -> notFound("Currency"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
