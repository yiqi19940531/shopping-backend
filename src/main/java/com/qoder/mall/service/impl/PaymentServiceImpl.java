package com.qoder.mall.service.impl;

import com.qoder.mall.common.util.LogKit;
import com.qoder.mall.service.IOrderService;
import com.qoder.mall.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final IOrderService orderService;

    @Async
    @Override
    public void mockPay(Long userId, String orderNo) {
        long startTime = System.currentTimeMillis();
        LogKit.info("PAY_START", "模拟支付开始", "orderNo", orderNo, "userId", userId);
        
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        try {
            orderService.payOrder(orderNo);
            long costMs = System.currentTimeMillis() - startTime;
            LogKit.audit("PAY_SUCCESS", "支付成功", "orderNo", orderNo, "userId", userId, "costMs", costMs);
        } catch (Exception e) {
            long costMs = System.currentTimeMillis() - startTime;
            LogKit.error("PAY_FAILED", "支付失败", e, "orderNo", orderNo, "userId", userId, "costMs", costMs);
        }
    }
}
