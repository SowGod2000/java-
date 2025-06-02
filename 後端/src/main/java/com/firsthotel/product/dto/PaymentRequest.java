package com.firsthotel.product.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String itemName;
    private int totalAmount;
}
