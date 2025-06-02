package com.firsthotel.product.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> cartItems;
    private double totalPrice;

    public Cart() {
        this.cartItems = new ArrayList<>();
        this.totalPrice = 0.0;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    // 🔧 補上 Getter，讓 totalPrice 能被序列化到前端
    public double getTotalPrice() {
        return totalPrice;
    }

    public double calculateTotalPrice() {
        totalPrice = cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum();
        return totalPrice;
    }

    public void addItem(CartItem item) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductId() == item.getProductId()) {
                cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
                calculateTotalPrice();
                return;
            }
        }
        cartItems.add(item);
        calculateTotalPrice();
    }

    public void removeItem(int productId) {
        cartItems.removeIf(item -> item.getProductId() == productId);
        calculateTotalPrice();
    }

    public void updateItemQuantity(int productId, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProductId() == productId) {
                item.setQuantity(quantity);
                break;
            }
        }
        calculateTotalPrice();
    }

    public void clearCart() {
        cartItems.clear();
        totalPrice = 0.0;
    }
}
