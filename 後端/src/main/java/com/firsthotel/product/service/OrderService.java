package com.firsthotel.product.service;

import com.firsthotel.product.dto.CartDto;
import com.firsthotel.product.dto.CartItemDto;
import com.firsthotel.product.dto.OrderDto;
import com.firsthotel.product.dto.OrderItemDto;
import com.firsthotel.product.model.Order;
import com.firsthotel.product.model.OrderItem;
import com.firsthotel.product.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * ✅ 正式建立訂單（接收購物車資料）
     */
    @Transactional
    public Order createOrder(CartDto cartDto) {
        if (cartDto == null || cartDto.getItems() == null || cartDto.getItems().isEmpty()) {
            throw new IllegalArgumentException("購物車為空，無法建立訂單！");
        }

        Order order = new Order();
        order.setCustomerName(cartDto.getCustomerName());
        order.setEmail(cartDto.getEmail());
        order.setOrderTime(LocalDateTime.now());
        order.setPaymentStatus("UNPAID");
        order.setOrderStatus("處理中"); // 預設值
        order.setShippingMethod(cartDto.getShippingMethod());
        order.setPaymentMethod(cartDto.getPaymentMethod());

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItemDto item : cartDto.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(item.getProductId());
            orderItem.setProductName(item.getProductName());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getPrice().intValue());
            orderItem.setImageUrl(item.getImageUrl());
            orderItem.setOrder(order);

            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            orderItems.add(orderItem);
        }

        order.setTotalAmount(total);
        order.setItems(orderItems);

        return orderRepository.save(order);
    }

    public Order createOrderMock(Order base) {
        CartDto cart = new CartDto();
        cart.setCustomerName(base.getCustomerName());
        cart.setEmail(base.getEmail());

        CartItemDto item = new CartItemDto();
        item.setProductId(1L);
        item.setProductName("測試商品");
        item.setQuantity(1);
        item.setPrice(BigDecimal.valueOf(1000));
        item.setImageUrl("https://via.placeholder.com/100");

        List<CartItemDto> items = new ArrayList<>();
        items.add(item);
        cart.setItems(items);

        return createOrder(cart);
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setCustomerName(order.getCustomerName());
        dto.setEmail(order.getEmail());
        dto.setOrderTime(order.getOrderTime());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setShippingMethod(order.getShippingMethod());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setMerchantTradeNo(order.getMerchantTradeNo());
        dto.setPaidAt(order.getPaidAt());

        List<OrderItemDto> itemDtos = order.getItems().stream().map(item -> {
            OrderItemDto i = new OrderItemDto();
            i.setProductId(item.getProductId());
            i.setProductName(item.getProductName());
            i.setQuantity(item.getQuantity());
            i.setPrice(item.getPrice());
            i.setImageUrl(item.getImageUrl());
            return i;
        }).collect(Collectors.toList());

        dto.setItems(itemDtos);
        return dto;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public List<Order> searchByCustomerNameOrEmail(String keyword) {
        return orderRepository.findByCustomerNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
    }

    public void cancelUnpaidOrdersBefore(LocalDateTime deadline) {
        List<Order> unpaidOrders = orderRepository.findByPaymentStatusAndOrderTimeBefore("UNPAID", deadline);
        for (Order order : unpaidOrders) {
            order.setPaymentStatus("CANCELLED");
            order.setOrderStatus("已取消");
        }
        orderRepository.saveAll(unpaidOrders);
    }

    public List<Order> findByEmail(String email) {
        return orderRepository.findByEmail(email);
    }

    /**
     * ✅ 單筆訂單取消邏輯（會員自己操作）
     */
    public boolean cancelOrder(Long orderId, String email) {
        Order order = findById(orderId);
        if (order == null || !order.getEmail().equals(email)) return false;
        if ("已出貨".equals(order.getOrderStatus()) || "已取消".equals(order.getOrderStatus())) return false;

        order.setOrderStatus("已取消");
        order.setPaymentStatus("CANCELLED");
        orderRepository.save(order);
        return true;
    }

    /**
     * ✅ 後台更新訂單狀態（例如出貨）
     */
    public boolean updateOrderStatus(Long orderId, String newStatus) {
        Order order = findById(orderId);
        if (order == null) return false;

        order.setOrderStatus(newStatus);
        orderRepository.save(order);
        return true;
    }
}
