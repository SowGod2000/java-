package com.firsthotel.member.bean;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class OperationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MEMBERID", referencedColumnName = "MEMBERID") // 外鍵關聯
    private Member member; // 這裡就是指向對應的會員

    private Long operatorId; // 操作人員ID (管理員)
    private String action; // 操作類型，例如 CREATE_MEMBER, UPDATE_MEMBER
    private String description; // 描述操作細節

    private String ipAddress; // 操作人的IP地址 (可選)
    private LocalDateTime createdAt; // 操作時間

    // getter 和 setter 省略
}

