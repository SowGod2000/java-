package com.firsthotel.product.dto;

import lombok.Data;

@Data
public class ProductDto {
    private int pid;
    private String pname;
    private int pprice;
    private String pimage;
    private String pstatus;
    private String ptags;
    private String pdescription;
    private String plistedAt;
    private String pupdatedAt;

    // ✅ 加入分類欄位
    private Long categoryId;
    private String categoryName;
}
