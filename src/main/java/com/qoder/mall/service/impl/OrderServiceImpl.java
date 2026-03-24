package com.qoder.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoder.mall.common.constant.OrderStatus;
import com.qoder.mall.common.exception.BusinessException;
import com.qoder.mall.common.util.OrderNoGenerator;
import com.qoder.mall.dto.request.OrderSubmitRequest;
import com.qoder.mall.entity.*;
import com.qoder.mall.mapper.*;
import com.qoder.mall.service.IOrderService;
import com.qoder.mall.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public OrderVO submitOrder(Long userId, OrderSubmitRequest request) {
        // 1. Load cart items
        List<CartItem> cartItems = cartItemMapper.selectList(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .in(CartItem::getId, request.getCartItemIds())
        );
        if (cartItems.isEmpty()) {
            throw new BusinessException("购物车中没有选中的商品");
        }

        // 2. Load address
        Address address = addressMapper.selectById(request.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("收货地址不存在");
        }

        // 3. Generate order
        String orderNo = OrderNoGenerator.generate(userId);
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 4. Deduct stock and prepare order items
        for (CartItem cartItem : cartItems) {
            Product product = productMapper.selectById(cartItem.getProductId());
            if (product == null || product.getStatus() == 0) {
                throw new BusinessException("商品[" + (product != null ? product.getName() : cartItem.getProductId()) + "]已下架");
            }

            int affected = productMapper.deductStock(product.getId(), cartItem.getQuantity());
            if (affected == 0) {
                throw new BusinessException("商品[" + product.getName() + "]库存不足");
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImageUrl(product.getCoverImageId() != null ? "/api/files/" + product.getCoverImageId() : null);
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalAmount(itemTotal);
            orderItems.add(orderItem);
        }

        // 5. Create order
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPaymentAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING_PAYMENT.name());
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress());
        order.setRemark(request.getRemark());
        orderMapper.insert(order);

        // 6. Create order items
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        // 7. Clear cart items
        cartItemMapper.deleteBatchIds(request.getCartItemIds());

        return toOrderVO(order, orderItems);
    }

    @Override
    public IPage<OrderVO> getOrderList(Long userId, String status, int pageNum, int pageSize) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId);
        if (StringUtils.hasText(status)) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);

        Page<Order> page = orderMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return page.convert(order -> {
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
            );
            return toOrderVO(order, items);
        });
    }

    @Override
    public OrderVO getOrderDetail(Long userId, String orderNo) {
        Order order = getOrderByNo(orderNo);
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
        );
        return toOrderVO(order, items);
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, String orderNo, String reason) {
        Order order = getOrderByNo(orderNo);
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (!OrderStatus.PENDING_PAYMENT.name().equals(order.getStatus())) {
            throw new BusinessException("只有待支付的订单可以取消");
        }

        // Restore stock
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
        );
        for (OrderItem item : items) {
            productMapper.restoreStock(item.getProductId(), item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED.name());
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(reason);
        orderMapper.updateById(order);
    }

    @Override
    public void confirmReceive(Long userId, String orderNo) {
        Order order = getOrderByNo(orderNo);
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (!OrderStatus.SHIPPED.name().equals(order.getStatus())) {
            throw new BusinessException("只有已发货的订单可以确认收货");
        }

        order.setStatus(OrderStatus.RECEIVED.name());
        order.setReceiveTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void payOrder(String orderNo) {
        Order order = getOrderByNo(orderNo);
        if (!OrderStatus.PENDING_PAYMENT.name().equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许支付");
        }

        order.setStatus(OrderStatus.PAID.name());
        order.setPaymentTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    // ---- Admin methods ----

    @Override
    public IPage<OrderVO> adminSearchOrders(String orderNo, Long userId, String status, int pageNum, int pageSize) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(orderNo)) {
            wrapper.like(Order::getOrderNo, orderNo);
        }
        if (userId != null) {
            wrapper.eq(Order::getUserId, userId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);

        Page<Order> page = orderMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return page.convert(order -> {
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
            );
            return toOrderVO(order, items);
        });
    }

    @Override
    public OrderVO adminGetOrderDetail(String orderNo) {
        Order order = getOrderByNo(orderNo);
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
        );
        return toOrderVO(order, items);
    }

    @Override
    public void shipOrder(String orderNo, String trackingNo) {
        Order order = getOrderByNo(orderNo);
        if (!OrderStatus.PAID.name().equals(order.getStatus())) {
            throw new BusinessException("只有已支付的订单可以发货");
        }

        order.setStatus(OrderStatus.SHIPPED.name());
        order.setTrackingNo(trackingNo);
        order.setShipTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    // ---- Helpers ----

    private Order getOrderByNo(String orderNo) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo)
        );
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    private OrderVO toOrderVO(Order order, List<OrderItem> items) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPaymentAmount(order.getPaymentAmount());
        vo.setStatus(order.getStatus());
        try {
            vo.setStatusDesc(OrderStatus.valueOf(order.getStatus()).getDescription());
        } catch (IllegalArgumentException e) {
            vo.setStatusDesc(order.getStatus());
        }
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setTrackingNo(order.getTrackingNo());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        vo.setPaymentTime(order.getPaymentTime());
        vo.setShipTime(order.getShipTime());

        if (items != null) {
            vo.setItems(items.stream().map(item -> {
                OrderVO.OrderItemVO itemVO = new OrderVO.OrderItemVO();
                itemVO.setProductId(item.getProductId());
                itemVO.setProductName(item.getProductName());
                itemVO.setProductImageUrl(item.getProductImageUrl());
                itemVO.setPrice(item.getPrice());
                itemVO.setQuantity(item.getQuantity());
                itemVO.setTotalAmount(item.getTotalAmount());
                return itemVO;
            }).collect(Collectors.toList()));
        }
        return vo;
    }
}
