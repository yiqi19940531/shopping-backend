package com.qoder.mall.service;

import com.qoder.mall.dto.request.CartAddRequest;
import com.qoder.mall.dto.response.CartItemResponse;

import java.util.List;

public interface ICartService {

    void addToCart(Long userId, CartAddRequest request);

    List<CartItemResponse> getCartItems(Long userId);

    void updateQuantity(Long userId, Long cartItemId, int quantity);

    void toggleSelect(Long userId, Long cartItemId, int isSelected);

    void deleteCartItem(Long userId, Long cartItemId);

    void batchDelete(Long userId, List<Long> ids);
}
