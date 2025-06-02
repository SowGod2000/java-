package com.firsthotel.product.service;

import com.firsthotel.product.dto.CartDto;
import com.firsthotel.product.dto.CartItemDto;
import com.firsthotel.product.model.Cart;
import com.firsthotel.product.model.CartItem;
import com.firsthotel.product.model.Product;
import com.firsthotel.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final Cart cart = new Cart();

    @Autowired
    private ProductRepository productRepository;

    // ✅ 每次取得購物車都重新計算總金額
    public Cart getCart() {
        cart.calculateTotalPrice();
        return cart;
    }

    public double getTotalPrice() {
        return cart.calculateTotalPrice();
    }

    public void addItemToCart(CartItem item) {
        Product product = productRepository.findById(item.getProductId()).orElse(null);
        if (product != null) {
            item.setProductName(product.getPname());
            item.setPrice(product.getPprice());
            item.setImageUrl(product.getPimage());
            cart.addItem(item);
        }
    }

    public void removeItem(int productId) {
        cart.removeItem(productId);
    }

    public void updateItemQuantity(int productId, int quantity) {
        if (quantity > 0) {
            cart.updateItemQuantity(productId, quantity);
        } else {
            cart.removeItem(productId);
        }
    }

    public void clearCart() {
        cart.clearCart();
    }

    public CartDto checkout(String customerName, String email) {
        CartDto cartDto = new CartDto();
        cartDto.setCustomerName(customerName);
        cartDto.setEmail(email);

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems == null || cartItems.isEmpty()) {
            cartDto.setItems(new ArrayList<>()); // 保底空 List
            return cartDto;
        }

        List<CartItemDto> itemDtos = cartItems.stream()
                .map(item -> new CartItemDto(
                        (long) item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        BigDecimal.valueOf(item.getPrice()),
                        item.getImageUrl()))
                .collect(Collectors.toList());

        cartDto.setItems(itemDtos);
        cart.clearCart(); // ✅ 清空購物車
        return cartDto;
    }

}
