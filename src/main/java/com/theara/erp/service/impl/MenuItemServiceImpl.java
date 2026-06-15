package com.theara.erp.service.impl;

import com.theara.erp.common.PageMapper;
import com.theara.erp.dto.request.MenuItemRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.MenuItemResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Company;
import com.theara.erp.entity.MenuItem;
import com.theara.erp.exception.ApiException;
import com.theara.erp.mapper.MenuItemMapper;
import com.theara.erp.repository.CompanyRepository;
import com.theara.erp.repository.MenuItemRepository;
import com.theara.erp.repository.ProductRepository;
import com.theara.erp.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j @Service @RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;
    private final MenuItemMapper menuItemMapper;

    @Override @Transactional
    public MenuItemResponse createMenuItem(MenuItemRequest request) {
        MenuItem menuItem = new MenuItem();
        apply(menuItem, request);
        return menuItemMapper.toResponse(menuItemRepository.save(menuItem));
    }

    @Override @Transactional(readOnly = true)
    public MenuItemResponse getMenuItemById(Long id) {
        return menuItemMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public MenuItemResponse updateMenuItem(Long id, MenuItemRequest request) {
        MenuItem menuItem = findById(id);
        apply(menuItem, request);
        return menuItemMapper.toResponse(menuItemRepository.save(menuItem));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<MenuItem, MenuItemResponse, Void> getMenuItems(PageAbleRequest<Void> request) {
        return PageMapper.toResponseWithoutImage(menuItemRepository.findAll(request.getPageAble()), menuItemMapper::toResponse);
    }

    @Override @Transactional
    public void deleteMenuItem(Long id) {
        MenuItem menuItem = findById(id);
        menuItem.setIsDeleted(1);
        menuItemRepository.save(menuItem);
    }

    private void apply(MenuItem m, MenuItemRequest r) {
        Company company = companyRepository.findById(r.getCompanyId())
                .orElseThrow(() -> ApiException.notFound("Company"));
        m.setCompany(company);
        m.setProduct(r.getProductId() == null ? null
                : productRepository.findById(r.getProductId()).orElseThrow(() -> ApiException.notFound("Product")));
        m.setName(r.getName());
        m.setImage(r.getImage());
        m.setCategory(r.getCategory());
        m.setPrice(r.getPrice());
        m.setHappyHourPrice(r.getHappyHourPrice());
        if (r.getIsAvailable() != null) m.setIsAvailable(r.getIsAvailable());
    }

    private MenuItem findById(Long id) {
        return menuItemRepository.findById(id).orElseThrow(() -> ApiException.notFound("MenuItem"));
    }
}
