package com.qoder.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoder.mall.common.exception.BusinessException;
import com.qoder.mall.dto.request.CartAddRequest;
import com.qoder.mall.dto.response.CartItemResponse;
import com.qoder.mall.entity.CartItem;
import com.qoder.mall.entity.Product;
import com.qoder.mall.mapper.CartItemMapper;
import com.qoder.mall.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 购物车服务层单元测试
 * Unit tests for Cart Service layer
 */
@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    // 测试用常量 / Test constants
    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;
    private static final Long PRODUCT_ID = 100L;
    private static final Long CART_ITEM_ID = 10L;

    /**
     * 创建测试用商品对象
     * Create a test Product object
     *
     * @param id 商品ID / product ID
     * @param status 商品状态(0下架/1上架) / product status (0=off-shelf/1=on-shelf)
     * @return 商品对象 / Product object
     */
    private Product createProduct(Long id, Integer status) {
        Product product = new Product();
        product.setId(id);
        product.setName("测试商品" + id);
        product.setPrice(new BigDecimal("99.99"));
        product.setStock(100);
        product.setStatus(status);
        product.setCoverImageId(1L);
        return product;
    }

    /**
     * 创建测试用购物车项对象
     * Create a test CartItem object
     *
     * @param id 购物车项ID / cart item ID
     * @param userId 用户ID / user ID
     * @param productId 商品ID / product ID
     * @param quantity 数量 / quantity
     * @return 购物车项对象 / CartItem object
     */
    private CartItem createCartItem(Long id, Long userId, Long productId, int quantity) {
        CartItem item = new CartItem();
        item.setId(id);
        item.setUserId(userId);
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setIsSelected(1);
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    /**
     * 创建添加购物车请求对象
     * Create a CartAddRequest object
     *
     * @param productId 商品ID / product ID
     * @param quantity 数量 / quantity
     * @return 请求对象 / request object
     */
    private CartAddRequest createCartAddRequest(Long productId, Integer quantity) {
        CartAddRequest request = new CartAddRequest();
        request.setProductId(productId);
        request.setQuantity(quantity);
        return request;
    }

    // ==================== addToCart 测试 / addToCart Tests ====================

    @Nested
    @DisplayName("addToCart - 添加商品到购物车 / Add product to cart")
    class AddToCartTests {

        @Test
        @DisplayName("正常添加新商品到购物车 - 应成功插入 / Add new product to cart - should insert successfully")
        void addToCart_newProduct_shouldInsert() {
            // 准备数据 / Arrange
            CartAddRequest request = createCartAddRequest(PRODUCT_ID, 2);
            Product product = createProduct(PRODUCT_ID, 1);

            when(productMapper.selectById(PRODUCT_ID)).thenReturn(product);
            when(cartItemMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
            when(cartItemMapper.insert(any(CartItem.class))).thenReturn(1);

            // 执行 / Act
            cartService.addToCart(USER_ID, request);

            // 验证 / Assert
            ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);
            verify(cartItemMapper).insert(captor.capture());

            CartItem inserted = captor.getValue();
            assertThat(inserted.getUserId()).isEqualTo(USER_ID);
            assertThat(inserted.getProductId()).isEqualTo(PRODUCT_ID);
            assertThat(inserted.getQuantity()).isEqualTo(2);
            assertThat(inserted.getIsSelected()).isEqualTo(1); // 默认选中 / default selected
        }

        @Test
        @DisplayName("商品已在购物车中 - 应累加数量 / Product already in cart - should add quantity")
        void addToCart_existingProduct_shouldAddQuantity() {
            // 准备数据 / Arrange
            CartAddRequest request = createCartAddRequest(PRODUCT_ID, 3);
            Product product = createProduct(PRODUCT_ID, 1);
            CartItem existing = createCartItem(CART_ITEM_ID, USER_ID, PRODUCT_ID, 2);

            when(productMapper.selectById(PRODUCT_ID)).thenReturn(product);
            when(cartItemMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existing);
            when(cartItemMapper.updateById(any(CartItem.class))).thenReturn(1);

            // 执行 / Act
            cartService.addToCart(USER_ID, request);

            // 验证 / Assert - 数量应从2增加到5 / quantity should increase from 2 to 5
            ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);
            verify(cartItemMapper).updateById(captor.capture());
            assertThat(captor.getValue().getQuantity()).isEqualTo(5);
            verify(cartItemMapper, never()).insert(any());
        }

        @Test
        @DisplayName("商品不存在 - 应抛出BusinessException / Product not found - should throw BusinessException")
        void addToCart_productNotFound_shouldThrowException() {
            // 准备数据 / Arrange
            CartAddRequest request = createCartAddRequest(PRODUCT_ID, 1);
            when(productMapper.selectById(PRODUCT_ID)).thenReturn(null);

            // 执行并验证 / Act & Assert
            assertThatThrownBy(() -> cartService.addToCart(USER_ID, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("商品不存在或已下架");

            verify(cartItemMapper, never()).insert(any());
            verify(cartItemMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("商品已下架(status=0) - 应抛出BusinessException / Product off-shelf - should throw BusinessException")
        void addToCart_productOffShelf_shouldThrowException() {
            // 准备数据 / Arrange
            CartAddRequest request = createCartAddRequest(PRODUCT_ID, 1);
            Product product = createProduct(PRODUCT_ID, 0); // 已下架 / off-shelf

            when(productMapper.selectById(PRODUCT_ID)).thenReturn(product);

            // 执行并验证 / Act & Assert
            assertThatThrownBy(() -> cartService.addToCart(USER_ID, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("商品不存在或已下架");

            verify(cartItemMapper, never()).insert(any());
        }

        @Test
        @DisplayName("BUG-1场景：商品status为null - 应触发NullPointerException导致500错误 / BUG-1: Product status is null - should cause NPE leading to 500 error")
        void addToCart_productStatusNull_shouldCauseNPE() {
            // 准备数据 / Arrange
            // 模拟商品的status字段为null的情况 / Simulate product with null status field
            CartAddRequest request = createCartAddRequest(PRODUCT_ID, 1);
            Product product = createProduct(PRODUCT_ID, null); // status为null / status is null

            when(productMapper.selectById(PRODUCT_ID)).thenReturn(product);

            // 执行并验证 / Act & Assert
            // product.getStatus() == 0 会抛出NullPointerException(自动拆箱)
            // product.getStatus() == 0 throws NullPointerException (auto-unboxing)
            // 这是BUG-1的一个潜在原因 / This is a potential cause of BUG-1
            assertThatThrownBy(() -> cartService.addToCart(USER_ID, request))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("BUG-1场景：数据库操作异常 - 应抛出运行时异常 / BUG-1: DB operation exception - should throw RuntimeException")
        void addToCart_dbError_shouldThrowRuntimeException() {
            // 准备数据 / Arrange
            CartAddRequest request = createCartAddRequest(PRODUCT_ID, 1);
            Product product = createProduct(PRODUCT_ID, 1);

            when(productMapper.selectById(PRODUCT_ID)).thenReturn(product);
            when(cartItemMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
            when(cartItemMapper.insert(any(CartItem.class)))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            // 执行并验证 / Act & Assert
            // 未捕获的RuntimeException会导致500错误 / Uncaught RuntimeException causes 500 error
            assertThatThrownBy(() -> cartService.addToCart(USER_ID, request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("数据库连接失败");
        }

        @Test
        @DisplayName("添加数量为1的商品 - 最小有效数量 / Add product with quantity 1 - minimum valid quantity")
        void addToCart_quantityOne_shouldSucceed() {
            // 准备数据 / Arrange
            CartAddRequest request = createCartAddRequest(PRODUCT_ID, 1);
            Product product = createProduct(PRODUCT_ID, 1);

            when(productMapper.selectById(PRODUCT_ID)).thenReturn(product);
            when(cartItemMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
            when(cartItemMapper.insert(any(CartItem.class))).thenReturn(1);

            // 执行 / Act
            cartService.addToCart(USER_ID, request);

            // 验证 / Assert
            ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);
            verify(cartItemMapper).insert(captor.capture());
            assertThat(captor.getValue().getQuantity()).isEqualTo(1);
        }
    }

    // ==================== getCartItems 测试 / getCartItems Tests ====================

    @Nested
    @DisplayName("getCartItems - 获取购物车列表 / Get cart items list")
    class GetCartItemsTests {

        @Test
        @DisplayName("正常获取购物车列表 - 应返回商品详情 / Get cart items normally - should return product details")
        void getCartItems_withItems_shouldReturnList() {
            // 准备数据 / Arrange
            CartItem item = createCartItem(CART_ITEM_ID, USER_ID, PRODUCT_ID, 2);
            Product product = createProduct(PRODUCT_ID, 1);

            when(cartItemMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(List.of(item));
            when(productMapper.selectById(PRODUCT_ID)).thenReturn(product);

            // 执行 / Act
            List<CartItemResponse> result = cartService.getCartItems(USER_ID);

            // 验证 / Assert
            assertThat(result).hasSize(1);
            CartItemResponse response = result.get(0);
            assertThat(response.getId()).isEqualTo(CART_ITEM_ID);
            assertThat(response.getProductId()).isEqualTo(PRODUCT_ID);
            assertThat(response.getProductName()).isEqualTo("测试商品" + PRODUCT_ID);
            assertThat(response.getProductPrice()).isEqualByComparingTo(new BigDecimal("99.99"));
            assertThat(response.getProductStock()).isEqualTo(100);
            assertThat(response.getQuantity()).isEqualTo(2);
            assertThat(response.getIsSelected()).isEqualTo(1);
            // 小计 = 99.99 * 2 = 199.98 / subtotal = 99.99 * 2 = 199.98
            assertThat(response.getSubtotal()).isEqualByComparingTo(new BigDecimal("199.98"));
            assertThat(response.getProductCoverUrl()).isEqualTo("/api/files/1");
        }

        @Test
        @DisplayName("购物车为空 - 应返回空列表 / Empty cart - should return empty list")
        void getCartItems_emptyCart_shouldReturnEmptyList() {
            // 准备数据 / Arrange
            when(cartItemMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());

            // 执行 / Act
            List<CartItemResponse> result = cartService.getCartItems(USER_ID);

            // 验证 / Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("购物车中商品已被删除 - 应显示'商品已删除' / Product deleted - should show '商品已删除'")
        void getCartItems_productDeleted_shouldShowDeletedMessage() {
            // 准备数据 / Arrange
            CartItem item = createCartItem(CART_ITEM_ID, USER_ID, PRODUCT_ID, 1);

            when(cartItemMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(List.of(item));
            when(productMapper.selectById(PRODUCT_ID)).thenReturn(null); // 商品已删除 / product deleted

            // 执行 / Act
            List<CartItemResponse> result = cartService.getCartItems(USER_ID);

            // 验证 / Assert
            assertThat(result).hasSize(1);
            CartItemResponse response = result.get(0);
            assertThat(response.getProductName()).isEqualTo("商品已删除");
            assertThat(response.getProductPrice()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(response.getProductStock()).isEqualTo(0);
            assertThat(response.getSubtotal()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(response.getProductCoverUrl()).isNull();
        }

        @Test
        @DisplayName("购物车中有多个商品 - 应返回所有商品 / Multiple items in cart - should return all")
        void getCartItems_multipleItems_shouldReturnAll() {
            // 准备数据 / Arrange
            CartItem item1 = createCartItem(1L, USER_ID, 100L, 2);
            CartItem item2 = createCartItem(2L, USER_ID, 101L, 3);

            Product product1 = createProduct(100L, 1);
            Product product2 = createProduct(101L, 1);
            product2.setPrice(new BigDecimal("49.99"));

            when(cartItemMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(List.of(item1, item2));
            when(productMapper.selectById(100L)).thenReturn(product1);
            when(productMapper.selectById(101L)).thenReturn(product2);

            // 执行 / Act
            List<CartItemResponse> result = cartService.getCartItems(USER_ID);

            // 验证 / Assert
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("商品无封面图 - coverUrl应为null / Product without cover image - coverUrl should be null")
        void getCartItems_productWithoutCover_shouldReturnNullCoverUrl() {
            // 准备数据 / Arrange
            CartItem item = createCartItem(CART_ITEM_ID, USER_ID, PRODUCT_ID, 1);
            Product product = createProduct(PRODUCT_ID, 1);
            product.setCoverImageId(null); // 无封面图 / no cover image

            when(cartItemMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(List.of(item));
            when(productMapper.selectById(PRODUCT_ID)).thenReturn(product);

            // 执行 / Act
            List<CartItemResponse> result = cartService.getCartItems(USER_ID);

            // 验证 / Assert
            assertThat(result.get(0).getProductCoverUrl()).isNull();
        }
    }

    // ==================== updateQuantity 测试 / updateQuantity Tests ====================

    @Nested
    @DisplayName("updateQuantity - 更新购物车数量 / Update cart item quantity")
    class UpdateQuantityTests {

        @Test
        @DisplayName("正常更新数量 - 应成功 / Update quantity normally - should succeed")
        void updateQuantity_validItem_shouldUpdate() {
            // 准备数据 / Arrange
            CartItem item = createCartItem(CART_ITEM_ID, USER_ID, PRODUCT_ID, 2);
            when(cartItemMapper.selectById(CART_ITEM_ID)).thenReturn(item);
            when(cartItemMapper.updateById(any(CartItem.class))).thenReturn(1);

            // 执行 / Act
            cartService.updateQuantity(USER_ID, CART_ITEM_ID, 5);

            // 验证 / Assert
            ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);
            verify(cartItemMapper).updateById(captor.capture());
            assertThat(captor.getValue().getQuantity()).isEqualTo(5);
        }

        @Test
        @DisplayName("购物车项不存在 - 应抛出BusinessException / Cart item not found - should throw BusinessException")
        void updateQuantity_itemNotFound_shouldThrow() {
            // 准备数据 / Arrange
            when(cartItemMapper.selectById(CART_ITEM_ID)).thenReturn(null);

            // 执行并验证 / Act & Assert
            assertThatThrownBy(() -> cartService.updateQuantity(USER_ID, CART_ITEM_ID, 5))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("购物车项不存在");
        }

        @Test
        @DisplayName("购物车项不属于当前用户 - 应抛出BusinessException / Cart item belongs to other user - should throw BusinessException")
        void updateQuantity_wrongUser_shouldThrow() {
            // 准备数据 / Arrange
            CartItem item = createCartItem(CART_ITEM_ID, OTHER_USER_ID, PRODUCT_ID, 2);
            when(cartItemMapper.selectById(CART_ITEM_ID)).thenReturn(item);

            // 执行并验证 / Act & Assert
            assertThatThrownBy(() -> cartService.updateQuantity(USER_ID, CART_ITEM_ID, 5))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("购物车项不存在");

            verify(cartItemMapper, never()).updateById(any());
        }
    }

    // ==================== toggleSelect 测试 / toggleSelect Tests ====================

    @Nested
    @DisplayName("toggleSelect - 切换选中状态 / Toggle select status")
    class ToggleSelectTests {

        @Test
        @DisplayName("正常切换为选中 - 应成功 / Toggle to selected - should succeed")
        void toggleSelect_toSelected_shouldUpdate() {
            // 准备数据 / Arrange
            CartItem item = createCartItem(CART_ITEM_ID, USER_ID, PRODUCT_ID, 1);
            item.setIsSelected(0);
            when(cartItemMapper.selectById(CART_ITEM_ID)).thenReturn(item);
            when(cartItemMapper.updateById(any(CartItem.class))).thenReturn(1);

            // 执行 / Act
            cartService.toggleSelect(USER_ID, CART_ITEM_ID, 1);

            // 验证 / Assert
            ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);
            verify(cartItemMapper).updateById(captor.capture());
            assertThat(captor.getValue().getIsSelected()).isEqualTo(1);
        }

        @Test
        @DisplayName("正常切换为未选中 - 应成功 / Toggle to unselected - should succeed")
        void toggleSelect_toUnselected_shouldUpdate() {
            // 准备数据 / Arrange
            CartItem item = createCartItem(CART_ITEM_ID, USER_ID, PRODUCT_ID, 1);
            when(cartItemMapper.selectById(CART_ITEM_ID)).thenReturn(item);
            when(cartItemMapper.updateById(any(CartItem.class))).thenReturn(1);

            // 执行 / Act
            cartService.toggleSelect(USER_ID, CART_ITEM_ID, 0);

            // 验证 / Assert
            ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);
            verify(cartItemMapper).updateById(captor.capture());
            assertThat(captor.getValue().getIsSelected()).isEqualTo(0);
        }

        @Test
        @DisplayName("购物车项不存在 - 应抛出BusinessException / Cart item not found - should throw BusinessException")
        void toggleSelect_itemNotFound_shouldThrow() {
            // 准备数据 / Arrange
            when(cartItemMapper.selectById(CART_ITEM_ID)).thenReturn(null);

            // 执行并验证 / Act & Assert
            assertThatThrownBy(() -> cartService.toggleSelect(USER_ID, CART_ITEM_ID, 1))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("购物车项不存在");
        }
    }

    // ==================== deleteCartItem 测试 / deleteCartItem Tests ====================

    @Nested
    @DisplayName("deleteCartItem - 删除购物车项 / Delete cart item")
    class DeleteCartItemTests {

        @Test
        @DisplayName("正常删除购物车项 - 应成功 / Delete cart item normally - should succeed")
        void deleteCartItem_validItem_shouldDelete() {
            // 准备数据 / Arrange
            CartItem item = createCartItem(CART_ITEM_ID, USER_ID, PRODUCT_ID, 1);
            when(cartItemMapper.selectById(CART_ITEM_ID)).thenReturn(item);
            when(cartItemMapper.deleteById(CART_ITEM_ID)).thenReturn(1);

            // 执行 / Act
            cartService.deleteCartItem(USER_ID, CART_ITEM_ID);

            // 验证 / Assert
            verify(cartItemMapper).deleteById(CART_ITEM_ID);
        }

        @Test
        @DisplayName("购物车项不存在 - 应抛出BusinessException / Cart item not found - should throw BusinessException")
        void deleteCartItem_itemNotFound_shouldThrow() {
            // 准备数据 / Arrange
            when(cartItemMapper.selectById(CART_ITEM_ID)).thenReturn(null);

            // 执行并验证 / Act & Assert
            assertThatThrownBy(() -> cartService.deleteCartItem(USER_ID, CART_ITEM_ID))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("购物车项不存在");

            verify(cartItemMapper, never()).deleteById(any(Long.class));
        }

        @Test
        @DisplayName("删除其他用户的购物车项 - 应抛出BusinessException / Delete other user's item - should throw BusinessException")
        void deleteCartItem_wrongUser_shouldThrow() {
            // 准备数据 / Arrange
            CartItem item = createCartItem(CART_ITEM_ID, OTHER_USER_ID, PRODUCT_ID, 1);
            when(cartItemMapper.selectById(CART_ITEM_ID)).thenReturn(item);

            // 执行并验证 / Act & Assert
            assertThatThrownBy(() -> cartService.deleteCartItem(USER_ID, CART_ITEM_ID))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("购物车项不存在");

            verify(cartItemMapper, never()).deleteById(any(Long.class));
        }
    }

    // ==================== batchDelete 测试 / batchDelete Tests ====================

    @Nested
    @DisplayName("batchDelete - 批量删除购物车项 / Batch delete cart items")
    class BatchDeleteTests {

        @Test
        @DisplayName("正常批量删除 - 应成功 / Batch delete normally - should succeed")
        void batchDelete_validIds_shouldDelete() {
            // 准备数据 / Arrange
            List<Long> ids = Arrays.asList(1L, 2L, 3L);
            when(cartItemMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(3);

            // 执行 / Act
            cartService.batchDelete(USER_ID, ids);

            // 验证 / Assert
            verify(cartItemMapper).delete(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("ids为空列表 - 应直接返回不执行删除 / Empty ids list - should return without deleting")
        void batchDelete_emptyList_shouldReturnEarly() {
            // 执行 / Act
            cartService.batchDelete(USER_ID, Collections.emptyList());

            // 验证 / Assert
            verify(cartItemMapper, never()).delete(any());
        }

        @Test
        @DisplayName("ids为null - 应直接返回不执行删除 / Null ids - should return without deleting")
        void batchDelete_nullList_shouldReturnEarly() {
            // 执行 / Act
            cartService.batchDelete(USER_ID, null);

            // 验证 / Assert
            verify(cartItemMapper, never()).delete(any());
        }
    }
}
