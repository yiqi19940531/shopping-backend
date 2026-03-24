package com.qoder.mall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoder.mall.dto.request.OrderSubmitRequest;
import com.qoder.mall.vo.OrderVO;

public interface IOrderService {

    OrderVO submitOrder(Long userId, OrderSubmitRequest request);

    IPage<OrderVO> getOrderList(Long userId, String status, int pageNum, int pageSize);

    OrderVO getOrderDetail(Long userId, String orderNo);

    void cancelOrder(Long userId, String orderNo, String reason);

    void confirmReceive(Long userId, String orderNo);

    void payOrder(String orderNo);

    // Admin methods
    IPage<OrderVO> adminSearchOrders(String orderNo, Long userId, String status, int pageNum, int pageSize);

    OrderVO adminGetOrderDetail(String orderNo);

    void shipOrder(String orderNo, String trackingNo);
}
