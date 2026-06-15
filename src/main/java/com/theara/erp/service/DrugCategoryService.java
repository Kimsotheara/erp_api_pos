package com.theara.erp.service;

import com.theara.erp.dto.request.DrugCategoryRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.DrugCategoryResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.DrugCategory;

public interface DrugCategoryService {
    DrugCategoryResponse createDrugCategory(DrugCategoryRequest request);
    DrugCategoryResponse getDrugCategoryById(Long id);
    DrugCategoryResponse updateDrugCategory(Long id, DrugCategoryRequest request);
    PageAbleResponse<DrugCategory, DrugCategoryResponse, Void> getDrugCategories(PageAbleRequest<Void> request);
    void deleteDrugCategory(Long id);
}
