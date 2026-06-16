package com.theara.erp.service;

import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.ProductRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.ProductResponse;
import com.theara.erp.entity.Product;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(Long id);

    ProductResponse getProductByBarcode(String barcode);

    ProductResponse updateProduct(Long id, ProductRequest request);

    PageAbleResponse<Product, ProductResponse, Void> getProducts(PageAbleRequest<Void> request);

    ProductResponse setActiveStatus(Long id, Boolean isActive);

    void deleteProduct(Long id);
}
