package com.qoder.mall.controller;

import com.qoder.mall.common.result.Result;
import com.qoder.mall.service.ICategoryService;
import com.qoder.mall.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "商品分类", description = "分类浏览相关接口")
public class CategoryController {

    private final ICategoryService categoryService;

    @GetMapping
    @Operation(summary = "获取分类树")
    public Result<List<CategoryVO>> getCategoryTree() {
        return Result.success(categoryService.getCategoryTree());
    }
}
