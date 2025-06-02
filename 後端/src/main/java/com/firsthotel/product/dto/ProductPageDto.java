package com.firsthotel.product.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductPageDto {
    private List<ProductDto> products;
    private int totalPages;
    private long totalElements;
}
