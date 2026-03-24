package com.qoder.mall.controller;

import com.qoder.mall.common.result.Result;
import com.qoder.mall.dto.request.CartAddRequest;
import com.qoder.mall.dto.response.CartItemResponse;
import com.qoder.mall.service.ICartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "购物车", description = "购物车相关接口")
public class CartController {

    private final ICartService cartService;

    @GetMapping
    @Operation(summary = "查看购物车")
    public Result<List<CartItemResponse>> getCart(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(cartService.getCartItems(userId));
    }

    @PostMapping
    @Operation(summary = "添加到购物车")
    public Result<Void> addToCart(@Valid @RequestBody CartAddRequest request,
                                 Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        cartService.addToCart(userId, request);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新购物车数量")
    public Result<Void> updateQuantity(@PathVariable Long id,
                                       @RequestParam int quantity,
                                       Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        cartService.updateQuantity(userId, id, quantity);
        return Result.success();
    }

    @PutMapping("/{id}/select")
    @Operation(summary = "切换选中状态")
    public Result<Void> toggleSelect(@PathVariable Long id,
                                     @RequestParam int isSelected,
                                     Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        cartService.toggleSelect(userId, id, isSelected);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除购物车项")
    public Result<Void> deleteCartItem(@PathVariable Long id,
                                       Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        cartService.deleteCartItem(userId, id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除购物车项")
    public Result<Void> batchDelete(@RequestBody List<Long> ids,
                                    Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        cartService.batchDelete(userId, ids);
        return Result.success();
    }
}
