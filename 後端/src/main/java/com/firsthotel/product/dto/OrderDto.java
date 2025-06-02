package com.firsthotel.product.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private String customerName;
    private String email;
    private LocalDateTime orderTime;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String orderStatus;       
    private String shippingMethod;    
    private String paymentMethod;     
    private String merchantTradeNo;
    private LocalDateTime paidAt;
    private List<OrderItemDto> items;
}
