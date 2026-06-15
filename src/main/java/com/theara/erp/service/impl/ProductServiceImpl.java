package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.ProductRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.ProductResponse;
import com.theara.erp.entity.*;
import com.theara.erp.mapper.ProductMapper;
import com.theara.erp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements com.theara.erp.service.ProductService {

    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final UnitRepository unitRepository;
    private final TaxRepository taxRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByCompanyIdAndSkuIgnoreCase(request.getCompanyId(), request.getSku())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "SKU '" + request.getSku() + "' " + ErrorCode.SKU_ALREADY_EXISTS.getDescription());
        }
        Product product = new Product();
        applyRequest(product, request);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return productMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findById(id);
        if (productRepository.existsByCompanyIdAndSkuIgnoreCaseAndIdNot(request.getCompanyId(), request.getSku(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "SKU '" + request.getSku() + "' " + ErrorCode.SKU_ALREADY_EXISTS.getDescription());
        }
        applyRequest(product, request);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public PageAbleResponse<Product, ProductResponse, Void> getProducts(PageAbleRequest<Void> request) {
        return com.theara.erp.common.PageMapper.toResponseWithoutImage(
                productRepository.findAll(request.getPageAble()), productMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = findById(id);
        product.setIsDeleted(1);
        productRepository.save(product);
    }

    /** Copies scalar fields and resolves foreign keys into managed references. */
    private void applyRequest(Product product, ProductRequest request) {
        product.setCompany(resolveCompany(request.getCompanyId()));
        product.setSku(request.getSku());
        product.setBarcode(request.getBarcode());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImage(request.getImage());
        product.setCategory(request.getCategoryId() == null ? null
                : categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> notFound("Category")));
        product.setBrand(request.getBrandId() == null ? null
                : brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> notFound("Brand")));
        product.setUnit(request.getUnitId() == null ? null
                : unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> notFound("Unit")));
        product.setTax(request.getTaxId() == null ? null
                : taxRepository.findById(request.getTaxId())
                .orElseThrow(() -> notFound("Tax")));
        if (request.getCostPrice() != null) product.setCostPrice(request.getCostPrice());
        if (request.getIsService() != null) product.setIsService(request.getIsService());
        if (request.getTrackStock() != null) product.setTrackStock(request.getTrackStock());
        product.setReorderLevel(request.getReorderLevel() != null ? request.getReorderLevel() : BigDecimal.ZERO);
        if (request.getIsActive() != null) product.setIsActive(request.getIsActive());
    }

    private Company resolveCompany(Long companyId) {
        return companyRepository.findById(companyId).orElseThrow(() -> notFound("Company"));
    }

    private Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> notFound("Product"));
    }

    private ResponseStatusException notFound(String entity) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, entity + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
