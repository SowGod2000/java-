package com.firsthotel.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartDto {
    private String customerName;
    private String email;
    private String shippingMethod;  // ✅ 新增
    private String paymentMethod;   // ✅ 新增
    private List<CartItemDto> items = new ArrayList<>();
}
