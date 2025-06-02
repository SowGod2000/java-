package com.firsthotel.product.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.firsthotel.member.bean.Member;

@Data
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "memberId", referencedColumnName = "MemberID")
    private Member member;

    private String customerName;
    private String email;

    private LocalDateTime orderTime;
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String paymentStatus = "UNPAID"; // UNPAID / PAID / FAIL / CANCELLED

    private String merchantTradeNo;
    private LocalDateTime paidAt;

    // ✅ 新增欄位
    private String orderStatus = "處理中";        // 處理中 / 已出貨 / 已取消
    private String shippingMethod;                // 宅配 / 7-11 取貨等
    private String paymentMethod;                 // 信用卡 / ATM等

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public String getOrderId() {
        return id != null ? id.toString() : null;
    }

    public int getAmount() {
        return totalAmount.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    public String getItemName() {
        if (items != null && !items.isEmpty()) {
            String firstName = items.get(0).getProductName();
            int others = items.size() - 1;
            return others > 0 ? firstName + " 等共 " + items.size() + " 項" : firstName;
        }
        return "購物車商品";
    }
}
