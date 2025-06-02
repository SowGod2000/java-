package com.firsthotel.product.controller;

import com.firsthotel.product.dto.ProductDto;
import com.firsthotel.product.dto.ProductPageDto;
import com.firsthotel.product.dto.ProductMapper;
import com.firsthotel.product.model.Product;
import com.firsthotel.product.repository.ProductRepository;
import com.firsthotel.product.service.ProductService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    public ProductController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<ProductPageDto> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String sort) {

        Page<Product> productPage = productService.getFilteredProducts(page, size, search, categoryId, sort);

        ProductPageDto dto = new ProductPageDto();
        dto.setProducts(ProductMapper.toDtoList(productPage.getContent()));
        dto.setTotalPages(productPage.getTotalPages());
        dto.setTotalElements(productPage.getTotalElements());

        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/{pid}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable int pid) {
        return productService.getProductById(pid)
                .map(product -> ResponseEntity.ok(ProductMapper.toDto(product)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody ProductDto dto) {
        Product product = convertToEntity(dto);
        return ResponseEntity.ok().body(productService.saveProduct(product, dto.getCategoryId()));
    }

    @PutMapping("/{pid}")
    public ResponseEntity<Product> updateProduct(@PathVariable int pid, @RequestBody ProductDto dto) {
        return productService.getProductById(pid)
                .map(product -> {
                    product.setPname(dto.getPname());
                    product.setPprice(dto.getPprice());
                    product.setPimage(dto.getPimage());
                    product.setPstatus(dto.getPstatus());
                    product.setPdescription(dto.getPdescription());
                    product.setPtags(dto.getPtags());
                    return ResponseEntity.ok(productService.saveProduct(product, dto.getCategoryId()));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{pid}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int pid) {
        return productService.deleteProduct(pid)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/tags")
    public ResponseEntity<List<Product>> getProductsByTag(@RequestParam String tag) {
        return ResponseEntity.ok(productRepository.findByPtagsContainingIgnoreCase(tag));
    }

    private Product convertToEntity(ProductDto dto) {
        Product product = new Product();
        product.setPname(dto.getPname());
        product.setPprice(dto.getPprice());
        product.setPimage(dto.getPimage());
        product.setPstatus(dto.getPstatus());
        product.setPdescription(dto.getPdescription());
        product.setPtags(dto.getPtags());
        return product;
    }
}
