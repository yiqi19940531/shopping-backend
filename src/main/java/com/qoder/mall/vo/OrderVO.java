package com.qoder.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "订单视图对象")
public class OrderVO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    @Schema(description = "实付金额")
    private BigDecimal paymentAmount;

    @Schema(description = "订单状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "收货人")
    private String receiverName;

    @Schema(description = "收货电话")
    private String receiverPhone;

    @Schema(description = "收货地址")
    private String receiverAddress;

    @Schema(description = "物流单号")
    private String trackingNo;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;

    @Schema(description = "发货时间")
    private LocalDateTime shipTime;

    @Schema(description = "订单明细")
    private List<OrderItemVO> items;

    @Data
    @Schema(description = "订单明细项")
    public static class OrderItemVO {
        @Schema(description = "商品ID")
        private Long productId;
        @Schema(description = "商品名称")
        private String productName;
        @Schema(description = "商品图片URL")
        private String productImageUrl;
        @Schema(description = "单价")
        private BigDecimal price;
        @Schema(description = "数量")
        private Integer quantity;
        @Schema(description = "小计")
        private BigDecimal totalAmount;
    }
}
