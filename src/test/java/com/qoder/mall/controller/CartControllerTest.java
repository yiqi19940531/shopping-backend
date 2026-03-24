package com.qoder.mall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qoder.mall.common.exception.BusinessException;
import com.qoder.mall.common.exception.GlobalExceptionHandler;
import com.qoder.mall.dto.request.CartAddRequest;
import com.qoder.mall.dto.response.CartItemResponse;
import com.qoder.mall.service.ICartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 购物车控制器层单元测试
 * Unit tests for Cart Controller layer
 *
 * 重点关注BUG-1: POST /api/cart 返回HTTP 500服务器内部错误
 * Focus on BUG-1: POST /api/cart returns HTTP 500 Internal Server Error
 */
@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private ICartService cartService;

    @InjectMocks
    private CartController cartController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    // 模拟已认证用户 / Mock authenticated user
    private static final Long USER_ID = 1L;
    private Authentication userAuth;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        userAuth = new UsernamePasswordAuthenticationToken(
                USER_ID, "testuser",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    // ==================================================================================
    // POST /api/cart - 添加到购物车 (BUG-1 重点测试) / Add to cart (BUG-1 Focus Tests)
    // ==================================================================================

    @Nested
    @DisplayName("POST /api/cart - 添加到购物车 / Add to cart (BUG-1 重点)")
    class AddToCartTests {

        @Test
        @DisplayName("TC-POST-01: 正常添加商品到购物车 - 应返回200成功 / Normal add to cart - should return 200 success")
        void addToCart_validRequest_shouldReturn200() throws Exception {
            // 准备数据 / Arrange
            CartAddRequest request = new CartAddRequest();
            request.setProductId(1L);
            request.setQuantity(2);

            doNothing().when(cartService).addToCart(eq(USER_ID), any(CartAddRequest.class));

            // 执行并验证 / Act & Assert
            mockMvc.perform(post("/api/cart")
                            .principal(userAuth)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("success"));

            verify(cartService).addToCart(eq(USER_ID), any(CartAddRequest.class));
        }

        @Test
        @DisplayName("TC-POST-02: productId为null - 应返回400参数验证失败 / productId is null - should return 400 validation error")
        void addToCart_nullProductId_shouldReturn400() throws Exception {
            // 准备数据 / Arrange
            CartAddRequest request = new CartAddRequest();
            request.setProductId(null);
            request.setQuantity(1);

            // 执行并验证 / Act & Assert
            mockMvc.perform(post("/api/cart")
                            .principal(userAuth)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value(containsString("商品ID不能为空")));

            verify(cartService, never()).addToCart(any(), any());
        }

        @Test
        @DisplayName("TC-POST-03: quantity为null - 应返回400参数验证失败 / quantity is null - should return 400 validation error")
        void addToCart_nullQuantity_shouldReturn400() throws Exception {
            // 准备数据 / Arrange
            CartAddRequest request = new CartAddRequest();
            request.setProductId(1L);
            request.setQuantity(null);

            // 执行并验证 / Act & Assert
            mockMvc.perform(post("/api/cart")
                            .principal(userAuth)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value(containsString("数量不能为空")));

            verify(cartService, never()).addToCart(any(), any());
        }

        @Test
        @DisplayName("TC-POST-04: quantity为0 - 应返回400数量至少为1 / quantity is 0 - should return 400 quantity at least 1")
        void addToCart_zeroQuantity_shouldReturn400() throws Exception {
            // 准备数据 / Arrange
            CartAddRequest request = new CartAddRequest();
            request.setProductId(1L);
            request.setQuantity(0);

            // 执行并验证 / Act & Assert
            mockMvc.perform(post("/api/cart")
                            .principal(userAuth)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value(containsString("数量至少为1")));

            verify(cartService, never()).addToCart(any(), any());
        }

        @Test
        @DisplayName("TC-POST-05: quantity为负数 - 应返回400数量至少为1 / quantity is negative - should return 400 quantity at least 1")
        void addToCart_negativeQuantity_shouldReturn400() throws Exception {
            // 准备数据 / Arrange
            CartAddRequest request = new CartAddRequest();
            request.setProductId(1L);
            request.setQuantity(-1);

            // 执行并验证 / Act & Assert
            mockMvc.perform(post("/api/cart")
                            .principal(userAuth)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value(containsString("数量至少为1")));
        }

        @Test
        @DisplayName("TC-POST-06: 商品不存在 - Service抛出BusinessException / Product not found - Service throws BusinessException")
        void addToCart_productNotFound_shouldReturnBusinessError() throws Exception {
            // 准备数据 / Arrange
            CartAddRequest request = new CartAddRequest();
            request.setProductId(999L);
            request.setQuantity(1);

            doThrow(new BusinessException("商品不存在或已下架"))
                    .when(cartService).addToCart(eq(USER_ID), any(CartAddRequest.class));

            // 执行并验证 / Act & Assert
            mockMvc.perform(post("/api/cart")
                            .principal(userAuth)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk()) // GlobalExceptionHandler未设置@ResponseStatus / no @ResponseStatus
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("商品不存在或已下架"));
        }

        @Test
        @DisplayName("TC-POST-07: BUG-1复现 - Service抛出RuntimeException应返回500 / BUG-1 Reproduce - Service throws RuntimeException should return 500")
        void addToCart_serviceThrowsRuntimeException_shouldReturn500() throws Exception {
            // 准备数据 / Arrange
            // 模拟服务层抛出未捕获的RuntimeException（如NullPointerException）
            // Simulate service throwing uncaught RuntimeException (e.g. NullPointerException)
            CartAddRequest request = new CartAddRequest();
            request.setProductId(1L);
            request.setQuantity(1);

            doThrow(new NullPointerException("product.getStatus() auto-unboxing NPE"))
                    .when(cartService).addToCart(eq(USER_ID), any(CartAddRequest.class));

            // 执行并验证 / Act & Assert
            // 这是BUG-1的典型表现：前端收到500错误
            // This is the typical manifestation of BUG-1: frontend receives 500 error
            mockMvc.perform(post("/api/cart")
                            .principal(userAuth)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("服务器内部错误"));
        }

        @Test
        @DisplayName("TC-POST-08: BUG-1场景 - 数据库异常导致500 / BUG-1 Scenario - Database exception causes 500")
        void addToCart_databaseException_shouldReturn500() throws Exception {
            // 准备数据 / Arrange
            CartAddRequest request = new CartAddRequest();
            request.setProductId(1L);
            request.setQuantity(1);

            doThrow(new RuntimeException("数据库连接超时"))
                    .when(cartService).addToCart(eq(USER_ID), any(CartAddRequest.class));

            // 执行并验证 / Act & Assert
            mockMvc.perform(post("/api/cart")
                            .principal(userAuth)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("服务器内部错误"));
        }

        @Test
        @DisplayName("TC-POST-09: 请求体为空JSON - 应返回400 / Empty JSON body - should return 400")
        void addToCart_emptyBody_shouldReturn400() throws Exception {
            // 执行并验证 / Act & Assert
            mockMvc.perform(post("/api/cart")
                            .principal(userAuth)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));

            verify(cartService, never()).addToCart(any(), any());
        }

        @Test
        @DisplayName("TC-POST-10: 未提供认证信息 - Authentication为null / No authentication - Authentication is null")
        void addToCart_noAuthentication_shouldFail() throws Exception {
            // 准备数据 / Arrange
            CartAddRequest request = new CartAddRequest();
            request.setProductId(1L);
            request.setQuantity(1);

            // 执行并验证 / Act & Assert
            // 不设置principal，authentication参数为null
            // Without setting principal, authentication parameter is null
            // 调用authentication.getPrincipal()会抛出NullPointerException
            // Calling authentication.getPrincipal() throws NullPointerException
            mockMvc.perform(post("/api/cart")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));
        }

        @Test
        @DisplayName("TC-POST-11: 添加大量数量商品 - 应成功 / Add large quantity - should succeed")
        void addToCart_largeQuantity_shouldReturn200() throws Exception {
            // 准备数据 / Arrange
            CartAddRequest request = new CartAddRequest();
            request.setProductId(1L);
            request.setQuantity(9999);

            doNothing().when(cartService).addToCart(eq(USER_ID), any(CartAddRequest.class));

            // 执行并验证 / Act & Assert
            mockMvc.perform(post("/api/cart")
                            .principal(userAuth)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    // ==================================================================================
    // GET /api/cart - 查看购物车 / View cart
    // ==================================================================================

    @Nested
    @DisplayName("GET /api/cart - 查看购物车 / View cart")
    class GetCartTests {

        @Test
        @DisplayName("TC-GET-01: 正常获取购物车列表 - 应返回200和数据 / Normal get cart - should return 200 with data")
        void getCart_authenticated_shouldReturn200() throws Exception {
            // 准备数据 / Arrange
            CartItemResponse item = CartItemResponse.builder()
                    .id(1L)
                    .productId(100L)
                    .productName("测试商品")
                    .productPrice(new BigDecimal("99.99"))
                    .productStock(50)
                    .productCoverUrl("/api/files/1")
                    .quantity(2)
                    .isSelected(1)
                    .subtotal(new BigDecimal("199.98"))
                    .build();

            when(cartService.getCartItems(USER_ID)).thenReturn(List.of(item));

            // 执行并验证 / Act & Assert
            mockMvc.perform(get("/api/cart")
                            .principal(userAuth))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data", hasSize(1)))
                    .andExpect(jsonPath("$.data[0].productId").value(100))
                    .andExpect(jsonPath("$.data[0].productName").value("测试商品"))
                    .andExpect(jsonPath("$.data[0].quantity").value(2))
                    .andExpect(jsonPath("$.data[0].isSelected").value(1));
        }

        @Test
        @DisplayName("TC-GET-02: 购物车为空 - 应返回200和空列表 / Empty cart - should return 200 with empty list")
        void getCart_emptyCart_shouldReturn200WithEmptyList() throws Exception {
            // 准备数据 / Arrange
            when(cartService.getCartItems(USER_ID)).thenReturn(Collections.emptyList());

            // 执行并验证 / Act & Assert
            mockMvc.perform(get("/api/cart")
                            .principal(userAuth))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data", hasSize(0)));
        }

        @Test
        @DisplayName("TC-GET-03: 未认证请求 - 应返回500(authentication为null) / Unauthenticated - should return 500 (null auth)")
        void getCart_noAuthentication_shouldFail() throws Exception {
            // 执行并验证 / Act & Assert
            mockMvc.perform(get("/api/cart"))
                    .andExpect(status().isInternalServerError());
        }
    }

    // ==================================================================================
    // PUT /api/cart/{id} - 更新购物车数量 / Update cart quantity
    // ==================================================================================

    @Nested
    @DisplayName("PUT /api/cart/{id} - 更新购物车数量 / Update cart quantity")
    class UpdateQuantityTests {

        @Test
        @DisplayName("TC-PUT-01: 正常更新数量 - 应返回200 / Normal update - should return 200")
        void updateQuantity_validRequest_shouldReturn200() throws Exception {
            // 准备数据 / Arrange
            doNothing().when(cartService).updateQuantity(USER_ID, 1L, 5);

            // 执行并验证 / Act & Assert
            mockMvc.perform(put("/api/cart/1")
                            .principal(userAuth)
                            .param("quantity", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(cartService).updateQuantity(USER_ID, 1L, 5);
        }

        @Test
        @DisplayName("TC-PUT-02: 购物车项不存在 - 应返回业务错误 / Cart item not found - should return business error")
        void updateQuantity_itemNotFound_shouldReturnError() throws Exception {
            // 准备数据 / Arrange
            doThrow(new BusinessException("购物车项不存在"))
                    .when(cartService).updateQuantity(USER_ID, 999L, 5);

            // 执行并验证 / Act & Assert
            mockMvc.perform(put("/api/cart/999")
                            .principal(userAuth)
                            .param("quantity", "5"))
                    .andExpect(status().isOk()) // BusinessException无@ResponseStatus / no @ResponseStatus
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("购物车项不存在"));
        }

        @Test
        @DisplayName("TC-PUT-03: 未认证请求 - 应返回500 / Unauthenticated - should return 500")
        void updateQuantity_noAuth_shouldFail() throws Exception {
            // 执行并验证 / Act & Assert
            mockMvc.perform(put("/api/cart/1")
                            .param("quantity", "5"))
                    .andExpect(status().isInternalServerError());
        }
    }

    // ==================================================================================
    // PUT /api/cart/{id}/select - 切换选中状态 / Toggle select status
    // ==================================================================================

    @Nested
    @DisplayName("PUT /api/cart/{id}/select - 切换选中状态 / Toggle select")
    class ToggleSelectTests {

        @Test
        @DisplayName("TC-SEL-01: 正常切换选中状态 - 应返回200 / Normal toggle - should return 200")
        void toggleSelect_validRequest_shouldReturn200() throws Exception {
            // 准备数据 / Arrange
            doNothing().when(cartService).toggleSelect(USER_ID, 1L, 1);

            // 执行并验证 / Act & Assert
            mockMvc.perform(put("/api/cart/1/select")
                            .principal(userAuth)
                            .param("isSelected", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(cartService).toggleSelect(USER_ID, 1L, 1);
        }

        @Test
        @DisplayName("TC-SEL-02: 购物车项不存在 - 应返回业务错误 / Item not found - should return business error")
        void toggleSelect_itemNotFound_shouldReturnError() throws Exception {
            // 准备数据 / Arrange
            doThrow(new BusinessException("购物车项不存在"))
                    .when(cartService).toggleSelect(USER_ID, 999L, 1);

            // 执行并验证 / Act & Assert
            mockMvc.perform(put("/api/cart/999/select")
                            .principal(userAuth)
                            .param("isSelected", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("购物车项不存在"));
        }

        @Test
        @DisplayName("TC-SEL-03: 未认证请求 - 应返回500 / Unauthenticated - should return 500")
        void toggleSelect_noAuth_shouldFail() throws Exception {
            // 执行并验证 / Act & Assert
            mockMvc.perform(put("/api/cart/1/select")
                            .param("isSelected", "1"))
                    .andExpect(status().isInternalServerError());
        }
    }

    // ==================================================================================
    // DELETE /api/cart/{id} - 删除购物车项 / Delete cart item
    // ==================================================================================

    @Nested
    @DisplayName("DELETE /api/cart/{id} - 删除购物车项 / Delete cart item")
    class DeleteCartItemTests {

        @Test
        @DisplayName("TC-DEL-01: 正常删除购物车项 - 应返回200 / Normal delete - should return 200")
        void deleteCartItem_validRequest_shouldReturn200() throws Exception {
            // 准备数据 / Arrange
            doNothing().when(cartService).deleteCartItem(USER_ID, 1L);

            // 执行并验证 / Act & Assert
            mockMvc.perform(delete("/api/cart/1")
                            .principal(userAuth))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(cartService).deleteCartItem(USER_ID, 1L);
        }

        @Test
        @DisplayName("TC-DEL-02: 购物车项不存在 - 应返回业务错误 / Item not found - should return business error")
        void deleteCartItem_itemNotFound_shouldReturnError() throws Exception {
            // 准备数据 / Arrange
            doThrow(new BusinessException("购物车项不存在"))
                    .when(cartService).deleteCartItem(USER_ID, 999L);

            // 执行并验证 / Act & Assert
            mockMvc.perform(delete("/api/cart/999")
                            .principal(userAuth))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("购物车项不存在"));
        }

        @Test
        @DisplayName("TC-DEL-03: 未认证请求 - 应返回500 / Unauthenticated - should return 500")
        void deleteCartItem_noAuth_shouldFail() throws Exception {
            // 执行并验证 / Act & Assert
            mockMvc.perform(delete("/api/cart/1"))
                    .andExpect(status().isInternalServerError());
        }
    }

    // ==================================================================================
    // DELETE /api/cart/batch - 批量删除购物车项 / Batch delete cart items
    // ==================================================================================

    @Nested
    @DisplayName("DELETE /api/cart/batch - 批量删除购物车项 / Batch delete")
    class BatchDeleteTests {

        @Test
        @DisplayName("TC-BATCH-01: 正常批量删除 - 应返回200 / Normal batch delete - should return 200")
        void batchDelete_validRequest_shouldReturn200() throws Exception {
            // 准备数据 / Arrange
            List<Long> ids = Arrays.asList(1L, 2L, 3L);
            doNothing().when(cartService).batchDelete(eq(USER_ID), eq(ids));

            // 执行并验证 / Act & Assert
            mockMvc.perform(delete("/api/cart/batch")
                            .principal(userAuth)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ids)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(cartService).batchDelete(eq(USER_ID), eq(ids));
        }

        @Test
        @DisplayName("TC-BATCH-02: 空列表批量删除 - 应返回200 / Empty list batch delete - should return 200")
        void batchDelete_emptyList_shouldReturn200() throws Exception {
            // 准备数据 / Arrange
            List<Long> ids = Collections.emptyList();
            doNothing().when(cartService).batchDelete(eq(USER_ID), eq(ids));

            // 执行并验证 / Act & Assert
            mockMvc.perform(delete("/api/cart/batch")
                            .principal(userAuth)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ids)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("TC-BATCH-03: 未认证请求 - 应返回500 / Unauthenticated - should return 500")
        void batchDelete_noAuth_shouldFail() throws Exception {
            // 准备数据 / Arrange
            List<Long> ids = Arrays.asList(1L, 2L);

            // 执行并验证 / Act & Assert
            mockMvc.perform(delete("/api/cart/batch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ids)))
                    .andExpect(status().isInternalServerError());
        }
    }
}
