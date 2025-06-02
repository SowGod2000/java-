package com.firsthotel.product.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.firsthotel.product.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Page<Product> findByPnameContainingIgnoreCase(String search, Pageable pageable);

    List<Product> findByPtagsContainingIgnoreCase(String tag);

    Page<Product> findByPnameContainingIgnoreCaseAndCategory_Id(String search, Long categoryId, Pageable pageable);

    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);
}
