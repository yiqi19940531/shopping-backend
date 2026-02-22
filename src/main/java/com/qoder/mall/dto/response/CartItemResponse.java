package com.qoder.mall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "购物车项响应")
public class CartItemResponse {

    @Schema(description = "购物车项ID")
    private Long id;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品价格")
    private BigDecimal productPrice;

    @Schema(description = "商品库存")
    private Integer productStock;

    @Schema(description = "商品封面图URL")
    private String productCoverUrl;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "是否选中")
    private Integer isSelected;

    @Schema(description = "小计金额")
    private BigDecimal subtotal;
}
