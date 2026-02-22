package com.qoder.mall.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoder.mall.common.result.Result;
import com.qoder.mall.dto.request.ProductSaveRequest;
import com.qoder.mall.entity.Product;
import com.qoder.mall.service.IAdminProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Tag(name = "后台-商品管理", description = "管理端商品CRUD接口")
public class AdminProductController {

    private final IAdminProductService adminProductService;

    @GetMapping
    @Operation(summary = "商品列表")
    public Result<IPage<Product>> list(
            @Parameter(description = "关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(adminProductService.getProductList(keyword, categoryId, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @Operation(summary = "商品详情")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.success(adminProductService.getProduct(id));
    }

    @PostMapping
    @Operation(summary = "新增商品")
    public Result<Product> create(@Valid @RequestBody ProductSaveRequest request) {
        return Result.success(adminProductService.createProduct(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新商品")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ProductSaveRequest request) {
        adminProductService.updateProduct(id, request);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "上下架")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam int status) {
        adminProductService.updateStatus(id, status);
        return Result.success();
    }

    @PutMapping("/{id}/stock")
    @Operation(summary = "调整库存")
    public Result<Void> updateStock(@PathVariable Long id, @RequestParam int stock) {
        adminProductService.updateStock(id, stock);
        return Result.success();
    }

    @PutMapping("/{id}/price")
    @Operation(summary = "调整价格")
    public Result<Void> updatePrice(@PathVariable Long id, @RequestParam BigDecimal price) {
        adminProductService.updatePrice(id, price);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品")
    public Result<Void> delete(@PathVariable Long id) {
        adminProductService.deleteProduct(id);
        return Result.success();
    }
}
