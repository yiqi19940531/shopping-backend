package com.qoder.mall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoder.mall.dto.request.ProductSaveRequest;
import com.qoder.mall.entity.Product;

import java.math.BigDecimal;

public interface IAdminProductService {

    IPage<Product> getProductList(String keyword, Long categoryId, int pageNum, int pageSize);

    Product getProduct(Long id);

    Product createProduct(ProductSaveRequest request);

    void updateProduct(Long id, ProductSaveRequest request);

    void updateStatus(Long id, int status);

    void updateStock(Long id, int stock);

    void updatePrice(Long id, BigDecimal price);

    void deleteProduct(Long id);
}
