package com.theara.erp.service;

import com.theara.erp.dto.request.MenuItemRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.MenuItemResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.MenuItem;

public interface MenuItemService {
    MenuItemResponse createMenuItem(MenuItemRequest request);
    MenuItemResponse getMenuItemById(Long id);
    MenuItemResponse updateMenuItem(Long id, MenuItemRequest request);
    PageAbleResponse<MenuItem, MenuItemResponse, Void> getMenuItems(PageAbleRequest<Void> request);
    void deleteMenuItem(Long id);
}
