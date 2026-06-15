package com.theara.erp.service;

import com.theara.erp.dto.request.IncomeRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.IncomeResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Income;

public interface IncomeService {
    IncomeResponse createIncome(IncomeRequest request);
    IncomeResponse getIncomeById(Long id);
    IncomeResponse updateIncome(Long id, IncomeRequest request);
    PageAbleResponse<Income, IncomeResponse, Void> getIncomes(PageAbleRequest<Void> request);
    void deleteIncome(Long id);
}
