package com.qoder.mall.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoder.mall.common.result.Result;
import com.qoder.mall.dto.request.ShipOrderRequest;
import com.qoder.mall.service.IOrderService;
import com.qoder.mall.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Tag(name = "后台-订单管理", description = "管理端订单检索/发货接口")
public class AdminOrderController {

    private final IOrderService orderService;

    @GetMapping
    @Operation(summary = "订单检索")
    public Result<IPage<OrderVO>> search(
            @Parameter(description = "订单号") @RequestParam(required = false) String orderNo,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "订单状态") @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(orderService.adminSearchOrders(orderNo, userId, status, pageNum, pageSize));
    }

    @GetMapping("/{orderNo}")
    @Operation(summary = "订单详情")
    public Result<OrderVO> detail(@PathVariable String orderNo) {
        return Result.success(orderService.adminGetOrderDetail(orderNo));
    }

    @PutMapping("/{orderNo}/ship")
    @Operation(summary = "发货")
    public Result<Void> ship(@PathVariable String orderNo,
                             @Valid @RequestBody ShipOrderRequest request) {
        orderService.shipOrder(orderNo, request.getTrackingNo());
        return Result.success();
    }
}
