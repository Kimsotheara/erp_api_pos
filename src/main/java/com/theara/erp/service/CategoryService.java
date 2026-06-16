package com.theara.erp.service;

import com.theara.erp.dto.request.CategoryRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.CategoryResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Category;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse getCategoryById(Long id);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    PageAbleResponse<Category, CategoryResponse, Void> getCategories(PageAbleRequest<Void> request);
    CategoryResponse setActiveStatus(Long id, Boolean isActive);
    void deleteCategory(Long id);
}
