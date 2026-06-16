package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.ActiveStatusRequest;
import com.theara.erp.dto.request.CategoryRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
@Tag(name = "Category", description = "Product category management")
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "List categories")
    @GetMapping
    public ResponseEntity<?> getCategories(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(categoryService.getCategories(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get category by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        return DefaultResponse.withCode(categoryService.getCategoryById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create category")
    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequest request) {
        return DefaultResponse.withCode(categoryService.createCategory(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update category")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return DefaultResponse.withCode(categoryService.updateCategory(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Activate/deactivate category", description = "Toggles the category's active status.")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> setCategoryStatus(@PathVariable Long id, @Valid @RequestBody ActiveStatusRequest request) {
        return DefaultResponse.withCode(categoryService.setActiveStatus(id, request.getIsActive()), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete category", description = "Soft-deletes a category.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
