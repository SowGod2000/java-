package com.firsthotel.product.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 關聯到訂單
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // 📦 商品資訊快照
    private Long productId;                // 商品原始 id（選填）
    private String productName;           // 商品名稱（購買當下）
    private int quantity;                 // 購買數量
    private int price;                    // 商品單價（TWD）
    private String imageUrl;              // 商品圖片

    // 👉 計算小計用的方法（非 DB 欄位）
    public int getSubtotal() {
        return price * quantity;
    }
}
