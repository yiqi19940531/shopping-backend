package com.qoder.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoder.mall.common.exception.BusinessException;
import com.qoder.mall.dto.request.ProductSaveRequest;
import com.qoder.mall.entity.Product;
import com.qoder.mall.entity.ProductImage;
import com.qoder.mall.mapper.ProductImageMapper;
import com.qoder.mall.mapper.ProductMapper;
import com.qoder.mall.service.IAdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements IAdminProductService {

    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;

    @Override
    public IPage<Product> getProductList(String keyword, Long categoryId, int pageNum, int pageSize) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        return productMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Product getProduct(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return product;
    }

    @Override
    @Transactional
    public Product createProduct(ProductSaveRequest request) {
        Product product = new Product();
        product.setSpuNo("SPU" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        copyFromRequest(product, request);
        product.setStock(request.getStock() != null ? request.getStock() : 0);
        product.setSales(0);
        product.setStatus(1);
        productMapper.insert(product);

        saveProductImages(product.getId(), request);
        return product;
    }

    @Override
    @Transactional
    public void updateProduct(Long id, ProductSaveRequest request) {
        Product product = getProduct(id);
        copyFromRequest(product, request);
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        productMapper.updateById(product);

        // Remove old images and add new ones
        productImageMapper.delete(
                new LambdaQueryWrapper<ProductImage>().eq(ProductImage::getProductId, id)
        );
        saveProductImages(id, request);
    }

    @Override
    public void updateStatus(Long id, int status) {
        Product product = getProduct(id);
        product.setStatus(status);
        productMapper.updateById(product);
    }

    @Override
    public void updateStock(Long id, int stock) {
        Product product = getProduct(id);
        product.setStock(stock);
        productMapper.updateById(product);
    }

    @Override
    public void updatePrice(Long id, BigDecimal price) {
        Product product = getProduct(id);
        product.setPrice(price);
        productMapper.updateById(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productMapper.deleteById(id);
    }

    private void copyFromRequest(Product product, ProductSaveRequest request) {
        product.setName(request.getName());
        product.setCategoryId(request.getCategoryId());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setCoverImageId(request.getCoverImageId());
        product.setDescription(request.getDescription());
        product.setDetail(request.getDetail());
        product.setIsHot(request.getIsHot() != null ? request.getIsHot() : 0);
        product.setIsRecommend(request.getIsRecommend() != null ? request.getIsRecommend() : 0);
    }

    private void saveProductImages(Long productId, ProductSaveRequest request) {
        if (request.getImageFileIds() != null) {
            for (int i = 0; i < request.getImageFileIds().size(); i++) {
                ProductImage image = new ProductImage();
                image.setProductId(productId);
                image.setFileId(request.getImageFileIds().get(i));
                image.setSortOrder(i);
                productImageMapper.insert(image);
            }
        }
    }
}
