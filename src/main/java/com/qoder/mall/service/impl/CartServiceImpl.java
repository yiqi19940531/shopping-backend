package com.qoder.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoder.mall.common.exception.BusinessException;
import com.qoder.mall.dto.request.CartAddRequest;
import com.qoder.mall.dto.response.CartItemResponse;
import com.qoder.mall.entity.CartItem;
import com.qoder.mall.entity.Product;
import com.qoder.mall.mapper.CartItemMapper;
import com.qoder.mall.mapper.ProductMapper;
import com.qoder.mall.service.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;

    @Override
    public void addToCart(Long userId, CartAddRequest request) {
        Product product = productMapper.selectById(request.getProductId());
        if (product == null || product.getStatus() == 0) {
            throw new BusinessException("商品不存在或已下架");
        }

        CartItem existing = cartItemMapper.selectOne(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .eq(CartItem::getProductId, request.getProductId())
        );

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            cartItemMapper.updateById(existing);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setIsSelected(1);
            cartItemMapper.insert(cartItem);
        }
    }

    @Override
    public List<CartItemResponse> getCartItems(Long userId) {
        List<CartItem> items = cartItemMapper.selectList(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .orderByDesc(CartItem::getCreateTime)
        );

        return items.stream().map(item -> {
            Product product = productMapper.selectById(item.getProductId());
            String coverUrl = product != null && product.getCoverImageId() != null
                    ? "/api/files/" + product.getCoverImageId() : null;

            return CartItemResponse.builder()
                    .id(item.getId())
                    .productId(item.getProductId())
                    .productName(product != null ? product.getName() : "商品已删除")
                    .productPrice(product != null ? product.getPrice() : BigDecimal.ZERO)
                    .productStock(product != null ? product.getStock() : 0)
                    .productCoverUrl(coverUrl)
                    .quantity(item.getQuantity())
                    .isSelected(item.getIsSelected())
                    .subtotal(product != null ? product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())) : BigDecimal.ZERO)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public void updateQuantity(Long userId, Long cartItemId, int quantity) {
        CartItem item = getAndVerifyOwnership(userId, cartItemId);
        item.setQuantity(quantity);
        cartItemMapper.updateById(item);
    }

    @Override
    public void toggleSelect(Long userId, Long cartItemId, int isSelected) {
        CartItem item = getAndVerifyOwnership(userId, cartItemId);
        item.setIsSelected(isSelected);
        cartItemMapper.updateById(item);
    }

    @Override
    public void deleteCartItem(Long userId, Long cartItemId) {
        getAndVerifyOwnership(userId, cartItemId);
        cartItemMapper.deleteById(cartItemId);
    }

    @Override
    public void batchDelete(Long userId, List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        cartItemMapper.delete(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .in(CartItem::getId, ids)
        );
    }

    private CartItem getAndVerifyOwnership(Long userId, Long cartItemId) {
        CartItem item = cartItemMapper.selectById(cartItemId);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new BusinessException("购物车项不存在");
        }
        return item;
    }
}
