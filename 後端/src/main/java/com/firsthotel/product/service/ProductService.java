package com.firsthotel.product.service;

import com.firsthotel.product.model.Product;
import com.firsthotel.product.model.Category;
import com.firsthotel.product.repository.ProductRepository;
import com.firsthotel.product.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<Product> getFilteredProducts(int page, int size, String search, Long categoryId, String sort) {
        Sort sorting = Sort.unsorted();
        if ("price_asc".equals(sort)) sorting = Sort.by(Sort.Direction.ASC, "pprice");
        else if ("price_desc".equals(sort)) sorting = Sort.by(Sort.Direction.DESC, "pprice");
        else if ("listed_asc".equals(sort)) sorting = Sort.by(Sort.Direction.ASC, "plistedAt");
        else if ("listed_desc".equals(sort)) sorting = Sort.by(Sort.Direction.DESC, "plistedAt");

        Pageable pageable = PageRequest.of(page, size, sorting);

        if (search != null && !search.isEmpty() && categoryId != null) {
            return productRepository.findByPnameContainingIgnoreCaseAndCategory_Id(search, categoryId, pageable);
        } else if (search != null && !search.isEmpty()) {
            return productRepository.findByPnameContainingIgnoreCase(search, pageable);
        } else if (categoryId != null) {
            return productRepository.findByCategory_Id(categoryId, pageable);
        } else {
            return productRepository.findAll(pageable);
        }
    }

    public Optional<Product> getProductById(int pid) {
        return productRepository.findById(pid);
    }

    public Product saveProduct(Product product, Long categoryId) {
        if (categoryId != null) {
            Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
            categoryOpt.ifPresent(product::setCategory);
        } else {
            product.setCategory(null);
        }
        return productRepository.save(product);
    }

    public boolean deleteProduct(int pid) {
        Optional<Product> optionalProduct = productRepository.findById(pid);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if ("ACTIVE".equalsIgnoreCase(product.getPstatus())) {
                return false;
            }
            productRepository.deleteById(pid);
            return true;
        }
        return false;
    }
} 
