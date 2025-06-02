package com.firsthotel.product.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.firsthotel.product.model.Favorite;
import com.firsthotel.product.repository.FavoriteRepository;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;

    public FavoriteController(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @PostMapping
    public ResponseEntity<?> addFavorite(@RequestBody Favorite favorite) {
        if (favoriteRepository.findByEmailAndProductId(favorite.getEmail(), favorite.getProductId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("已在最愛中");
        }
        return ResponseEntity.ok(favoriteRepository.save(favorite));
    }

    @DeleteMapping
    public ResponseEntity<?> removeFavorite(@RequestParam String email, @RequestParam Long productId) {
        favoriteRepository.deleteByEmailAndProductId(email, productId);
        return ResponseEntity.ok("已移除");
    }

    @GetMapping
    public ResponseEntity<List<Favorite>> getFavorites(@RequestParam String email) {
        return ResponseEntity.ok(favoriteRepository.findByEmail(email));
    }
}
