package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 进货记录实体类
 */
@Data
@TableName("purchases")
public class Purchase {
    @TableId(type = IdType.AUTO)
    private Long purchaseId;
    
    private String purchaseNumber;
    private String supplierName;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime purchaseDate;
    private Long operatorId;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;    // 构造方法
    public Purchase() {
        this.totalAmount = BigDecimal.ZERO;
        this.status = "pending";
        this.purchaseDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
