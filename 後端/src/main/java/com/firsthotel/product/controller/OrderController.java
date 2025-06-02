package com.firsthotel.product.controller;

import com.firsthotel.product.dto.CartDto;
import com.firsthotel.product.dto.OrderDto;
import com.firsthotel.product.model.Order;
import com.firsthotel.product.service.CartService;
import com.firsthotel.product.service.OrderService;
import com.firsthotel.member.service.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ 建立訂單
    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> checkout(@RequestBody CartDto cartDto) {
        try {
            Order order = orderService.createOrder(cartDto);
            cartService.clearCart();
            return ResponseEntity.ok(orderService.toDto(order));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ 取得單一訂單
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        Order order = orderService.findById(id);
        return order != null ? ResponseEntity.ok(orderService.toDto(order)) : ResponseEntity.notFound().build();
    }
    
    

    // ✅ 查詢所有訂單（後台）
    @GetMapping("/all")
    public ResponseEntity<List<OrderDto>> getAllOrders(@RequestParam(required = false) String search) {
        List<Order> orders = (search != null && !search.isBlank())
                ? orderService.searchByCustomerNameOrEmail(search)
                : orderService.findAll();

        List<OrderDto> dtos = orders.stream()
                .map(orderService::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // ✅ 取消30分鐘前未付款的訂單
    @GetMapping("/cancel-expired")
    public ResponseEntity<Void> cancelExpiredOrders() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(30);
        orderService.cancelUnpaidOrdersBefore(deadline);
        return ResponseEntity.ok().build();
    }

    // ✅ 查詢會員所有訂單
    @GetMapping("/member")
    public ResponseEntity<List<OrderDto>> getOrdersByMember(@CookieValue("jwt") String jwt) {
        try {
            String email = jwtUtil.extractUsername(jwt);
            List<Order> orders = orderService.findByEmail(email);
            List<OrderDto> dtos = orders.stream().map(orderService::toDto).collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    // ✅ 查詢會員單筆訂單（安全驗證）
    @GetMapping("/member/{orderId}")
    public ResponseEntity<OrderDto> getOrderByMember(
            @CookieValue("jwt") String jwt,
            @PathVariable Long orderId) {
        try {
            String email = jwtUtil.extractUsername(jwt);
            Order order = orderService.findById(orderId);

            if (order == null) return ResponseEntity.notFound().build();
            if (!order.getEmail().equals(email)) return ResponseEntity.status(403).build();

            return ResponseEntity.ok(orderService.toDto(order));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    // ✅ 會員取消訂單
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId, @CookieValue("jwt") String jwt) {
        try {
            String email = jwtUtil.extractUsername(jwt);
            boolean success = orderService.cancelOrder(orderId, email);
            return success ? ResponseEntity.ok().build() : ResponseEntity.status(403).body("無法取消訂單");
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    // ✅ 後台修改訂單狀態（例如：已出貨）
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String value) {
        boolean updated = orderService.updateOrderStatus(orderId, value);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body("修改失敗");
    }
}
