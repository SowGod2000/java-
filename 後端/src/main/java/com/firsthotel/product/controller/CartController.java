package com.firsthotel.product.controller;

import com.firsthotel.product.dto.CartDto;
import com.firsthotel.product.model.Cart;
import com.firsthotel.product.model.CartItem;
import com.firsthotel.product.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // 獲取購物車中的所有商品
    @GetMapping
    public ResponseEntity<Cart> getCartItems() {
        return ResponseEntity.ok(cartService.getCart());
    }

    // 添加商品到購物車
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody CartItem cartItem) {
        cartService.addItemToCart(cartItem);
        return ResponseEntity.ok("商品已成功加入購物車");
    }

    // 結帳，接收使用者資訊
    @PostMapping("/checkout")
    public ResponseEntity<CartDto> checkout(@RequestBody CartDto input) {
        CartDto result = cartService.checkout(input.getCustomerName(), input.getEmail());
        return ResponseEntity.ok(result);
    }

    // 刪除商品
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeItem(@PathVariable int productId) {
        cartService.removeItem(productId);
        return ResponseEntity.ok("商品已從購物車移除");
    }

    // 更新商品數量
    @PutMapping("/updateQuantity/{productId}")
    public ResponseEntity<String> updateItemQuantity(@PathVariable int productId, @RequestParam int quantity) {
        cartService.updateItemQuantity(productId, quantity);
        return ResponseEntity.ok("商品數量已更新");
    }

    // 清空購物車
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart() {
        cartService.clearCart();
        return ResponseEntity.ok("購物車已清空");
    }
}
