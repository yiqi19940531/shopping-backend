package com.qoder.mall.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoder.mall.common.result.Result;
import com.qoder.mall.dto.response.ProductDetailResponse;
import com.qoder.mall.service.IProductService;
import com.qoder.mall.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "商品浏览", description = "商品列表/详情相关接口")
public class ProductController {

    private final IProductService productService;

    @GetMapping("/hot")
    @Operation(summary = "获取热门商品")
    public Result<List<ProductVO>> getHotProducts(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") int limit) {
        return Result.success(productService.getHotProducts(limit));
    }

    @GetMapping("/recommend")
    @Operation(summary = "获取推荐商品")
    public Result<List<ProductVO>> getRecommendProducts(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") int limit) {
        return Result.success(productService.getRecommendProducts(limit));
    }

    @GetMapping
    @Operation(summary = "商品列表(分页+搜索)")
    public Result<IPage<ProductVO>> getProductList(
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(productService.getProductList(categoryId, keyword, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @Operation(summary = "商品详情")
    public Result<ProductDetailResponse> getProductDetail(@PathVariable Long id) {
        return Result.success(productService.getProductDetail(id));
    }
}
