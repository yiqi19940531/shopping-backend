package com.qoder.mall.controller;

import com.qoder.mall.common.result.Result;
import com.qoder.mall.service.IPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Tag(name = "支付(模拟)", description = "模拟支付接口")
public class PaymentController {

    private final IPaymentService paymentService;

    @PostMapping("/pay")
    @Operation(summary = "发起支付(模拟3秒延迟)")
    public Result<String> pay(@RequestParam String orderNo,
                              Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        paymentService.mockPay(userId, orderNo);
        return Result.success("支付处理中，请稍后查询订单状态");
    }
}
