package com.firsthotel.product.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "products")
public class Product {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pid;

    @Column(nullable = false, length = 100)
    private String pname;

    @Column(length = 255)
    private String pdescription;

    @Column(nullable = false)
    private int pprice;

    @Column(nullable = false, length = 500)
    private String pimage;

    @Column(nullable = false)
    private String pstatus;

    @Column(length = 255)
    private String ptags; // 逗號分隔的標籤，如："熱銷,純素,限量"

    @Column(nullable = false)
    private LocalDateTime plistedAt;

    @Column(nullable = false)
    private LocalDateTime pupdatedAt;

    @PrePersist
    protected void onCreate() {
        this.plistedAt = LocalDateTime.now();
        this.pupdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.pupdatedAt = LocalDateTime.now();
    }

    // ✅ 提供 categoryId 給 DTO 用
    public Long getCategoryId() {
        return (category != null) ? category.getId() : null;
    }

    // ✅ 提供 categoryName 給 DTO 用
    public String getCategoryName() {
        return (category != null) ? category.getName() : null;
    }
}
