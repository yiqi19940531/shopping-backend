package com.qoder.mall.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "提交订单请求")
public class OrderSubmitRequest {

    @NotEmpty(message = "请选择购物车商品")
    @Schema(description = "购物车项ID列表")
    private List<Long> cartItemIds;

    @NotNull(message = "请选择收货地址")
    @Schema(description = "收货地址ID", example = "1")
    private Long addressId;

    @Schema(description = "订单备注", example = "请尽快发货")
    private String remark;
}
