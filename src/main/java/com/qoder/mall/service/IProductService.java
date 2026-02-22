package com.qoder.mall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoder.mall.dto.response.ProductDetailResponse;
import com.qoder.mall.vo.ProductVO;

import java.util.List;

public interface IProductService {

    List<ProductVO> getHotProducts(int limit);

    List<ProductVO> getRecommendProducts(int limit);

    IPage<ProductVO> getProductList(Long categoryId, String keyword, int pageNum, int pageSize);

    ProductDetailResponse getProductDetail(Long id);
}
