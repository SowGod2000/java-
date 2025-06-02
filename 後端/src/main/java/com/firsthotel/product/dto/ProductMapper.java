package com.firsthotel.product.dto;

import com.firsthotel.product.model.Product;


import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;



public class ProductMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setPid(product.getPid());
        dto.setPname(product.getPname());
        dto.setPprice(product.getPprice());
        dto.setPimage(product.getPimage());
        dto.setPstatus(product.getPstatus());
        dto.setPtags(product.getPtags());
        dto.setPdescription(product.getPdescription());
        dto.setPlistedAt(product.getPlistedAt().format(formatter));
        dto.setPupdatedAt(product.getPupdatedAt().format(formatter));
        dto.setCategoryId(product.getCategoryId());
        dto.setCategoryName(product.getCategoryName());
        return dto;
    }

    public static List<ProductDto> toDtoList(List<Product> products) {
        return products.stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }
}
