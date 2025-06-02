package com.firsthotel.product.dto;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long productId;
    private String productName;
    private int quantity;
    private int price;
    private String imageUrl;
}
