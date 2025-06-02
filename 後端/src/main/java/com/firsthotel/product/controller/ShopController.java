package com.firsthotel.product.controller;

import com.firsthotel.product.model.Product;
import com.firsthotel.product.service.ShopService;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ShopController {
    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping("/api/products")
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        
        Page<Product> products = (search != null && !search.isEmpty()) 
            ? shopService.searchProducts(page, size, search)
            : shopService.getProducts(page, size);

        return ResponseEntity.ok(products.getContent());
    }
}
