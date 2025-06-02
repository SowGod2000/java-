package com.firsthotel.product.service;

import com.firsthotel.product.model.Product;
import com.firsthotel.product.repository.ProductRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ShopService {
    private final ProductRepository productRepository;

    public ShopService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 取得所有商品（分頁）
    public Page<Product> getProducts(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size));
    }

    // 搜尋商品（根據名稱）
    public Page<Product> searchProducts(int page, int size, String keyword) {
        return productRepository.findByPnameContainingIgnoreCase(keyword, PageRequest.of(page, size));
    }
}
