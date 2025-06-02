package com.firsthotel.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.firsthotel.product.model.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByEmail(String email);
    Optional<Favorite> findByEmailAndProductId(String email, Long productId);
    void deleteByEmailAndProductId(String email, Long productId);
}
