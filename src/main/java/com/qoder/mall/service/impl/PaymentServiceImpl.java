package com.qoder.mall.service.impl;

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
        log.info("模拟支付开始: orderNo={}", orderNo);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        try {
            orderService.payOrder(orderNo);
            log.info("模拟支付成功: orderNo={}", orderNo);
        } catch (Exception e) {
            log.error("模拟支付失败: orderNo={}, error={}", orderNo, e.getMessage());
        }
    }
}
