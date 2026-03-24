package com.qoder.mall.common.constant;

import lombok.Getter;

@Getter
public enum OrderStatus {

    PENDING_PAYMENT("待支付"),
    PAID("已支付"),
    SHIPPED("已发货"),
    RECEIVED("已收货"),
    COMPLETED("已完成"),
    CANCELLED("已取消");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}
