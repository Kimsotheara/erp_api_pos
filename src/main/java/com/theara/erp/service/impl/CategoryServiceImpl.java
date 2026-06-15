package com.theara.erp.service.impl;

import com.theara.erp.common.PageMapper;
import com.theara.erp.dto.request.CategoryRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.CategoryResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Category;
import com.theara.erp.entity.Company;
import com.theara.erp.exception.ApiException;
import com.theara.erp.mapper.CategoryMapper;
import com.theara.erp.repository.CategoryRepository;
import com.theara.erp.repository.CompanyRepository;
import com.theara.erp.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j @Service @RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CompanyRepository companyRepository;
    private final CategoryMapper categoryMapper;

    @Override @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        apply(category, request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        return categoryMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = findById(id);
        apply(category, request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<Category, CategoryResponse, Void> getCategories(PageAbleRequest<Void> request) {
        return PageMapper.toResponse(categoryRepository.findAll(request.getPageAble()), categoryMapper::toResponse);
    }

    @Override @Transactional
    public void deleteCategory(Long id) {
        Category category = findById(id);
        category.setIsDeleted(1);
        categoryRepository.save(category);
    }

    private void apply(Category c, CategoryRequest r) {
        Company company = companyRepository.findById(r.getCompanyId())
                .orElseThrow(() -> ApiException.notFound("Company"));
        c.setCompany(company);
        if (r.getParentId() != null) {
            if (r.getParentId().equals(c.getId())) {
                throw ApiException.badRequest("Category cannot be its own parent");
            }
            c.setParent(categoryRepository.findById(r.getParentId())
                    .orElseThrow(() -> ApiException.notFound("Parent category")));
        } else {
            c.setParent(null);
        }
        c.setName(r.getName());
        if (r.getIsActive() != null) c.setIsActive(r.getIsActive());
    }

    private Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> ApiException.notFound("Category"));
    }
}
