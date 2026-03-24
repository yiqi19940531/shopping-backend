package com.qoder.mall.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoder.mall.common.result.Result;
import com.qoder.mall.dto.request.OrderSubmitRequest;
import com.qoder.mall.service.IOrderService;
import com.qoder.mall.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "用户订单", description = "订单提交/查询/取消/收货接口")
public class OrderController {

    private final IOrderService orderService;

    @PostMapping
    @Operation(summary = "提交订单")
    public Result<OrderVO> submitOrder(@Valid @RequestBody OrderSubmitRequest request,
                                       Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(orderService.submitOrder(userId, request));
    }

    @GetMapping
    @Operation(summary = "订单列表")
    public Result<IPage<OrderVO>> getOrderList(
            @Parameter(description = "订单状态") @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(orderService.getOrderList(userId, status, pageNum, pageSize));
    }

    @GetMapping("/{orderNo}")
    @Operation(summary = "订单详情")
    public Result<OrderVO> getOrderDetail(@PathVariable String orderNo,
                                          Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(orderService.getOrderDetail(userId, orderNo));
    }

    @PutMapping("/{orderNo}/cancel")
    @Operation(summary = "取消订单")
    public Result<Void> cancelOrder(@PathVariable String orderNo,
                                    @RequestParam(required = false) String reason,
                                    Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        orderService.cancelOrder(userId, orderNo, reason);
        return Result.success();
    }

    @PutMapping("/{orderNo}/receive")
    @Operation(summary = "确认收货")
    public Result<Void> confirmReceive(@PathVariable String orderNo,
                                       Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        orderService.confirmReceive(userId, orderNo);
        return Result.success();
    }
}
