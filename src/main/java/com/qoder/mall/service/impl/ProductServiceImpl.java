package com.qoder.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoder.mall.common.exception.BusinessException;
import com.qoder.mall.dto.response.ProductDetailResponse;
import com.qoder.mall.entity.Product;
import com.qoder.mall.entity.ProductImage;
import com.qoder.mall.mapper.ProductImageMapper;
import com.qoder.mall.mapper.ProductMapper;
import com.qoder.mall.service.IProductService;
import com.qoder.mall.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;

    @Override
    public List<ProductVO> getHotProducts(int limit) {
        List<Product> products = productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getStatus, 1)
                        .eq(Product::getIsHot, 1)
                        .orderByDesc(Product::getSales)
                        .last("LIMIT " + limit)
        );
        return products.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<ProductVO> getRecommendProducts(int limit) {
        List<Product> products = productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getStatus, 1)
                        .eq(Product::getIsRecommend, 1)
                        .orderByDesc(Product::getCreateTime)
                        .last("LIMIT " + limit)
        );
        return products.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public IPage<ProductVO> getProductList(Long categoryId, String keyword, int pageNum, int pageSize) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1);

        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword);
        }
        wrapper.orderByDesc(Product::getCreateTime);

        Page<Product> page = productMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        return page.convert(this::toVO);
    }

    @Override
    public ProductDetailResponse getProductDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null || product.getStatus() == 0) {
            throw new BusinessException("商品不存在或已下架");
        }

        ProductDetailResponse response = new ProductDetailResponse();
        response.setId(product.getId());
        response.setSpuNo(product.getSpuNo());
        response.setName(product.getName());
        response.setCategoryId(product.getCategoryId());
        response.setBrand(product.getBrand());
        response.setPrice(product.getPrice());
        response.setOriginalPrice(product.getOriginalPrice());
        response.setStock(product.getStock());
        response.setSales(product.getSales());
        response.setDescription(product.getDescription());
        response.setDetail(product.getDetail());
        response.setIsHot(product.getIsHot());
        response.setIsRecommend(product.getIsRecommend());

        // Cover image
        if (product.getCoverImageId() != null) {
            response.setCoverImageUrl("/api/files/" + product.getCoverImageId());
        }

        // Carousel images
        List<ProductImage> images = productImageMapper.selectList(
                new LambdaQueryWrapper<ProductImage>()
                        .eq(ProductImage::getProductId, id)
                        .orderByAsc(ProductImage::getSortOrder)
        );
        List<String> imageUrls = images.stream()
                .map(img -> "/api/files/" + img.getFileId())
                .collect(Collectors.toList());
        response.setImageUrls(imageUrls);

        return response;
    }

    private ProductVO toVO(Product product) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setSpuNo(product.getSpuNo());
        vo.setName(product.getName());
        vo.setCategoryId(product.getCategoryId());
        vo.setBrand(product.getBrand());
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setStock(product.getStock());
        vo.setSales(product.getSales());
        vo.setDescription(product.getDescription());
        vo.setIsHot(product.getIsHot());
        vo.setIsRecommend(product.getIsRecommend());
        if (product.getCoverImageId() != null) {
            vo.setCoverImageUrl("/api/files/" + product.getCoverImageId());
        }
        return vo;
    }
}
