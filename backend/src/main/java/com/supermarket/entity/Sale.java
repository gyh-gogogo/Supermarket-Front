package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 销售记录实体类
 */
@Data
@TableName("sales")
public class Sale {
    
    @TableId(type = IdType.AUTO)
    private Long saleId;
    
    private String orderNumber;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String paymentMethod;
    private Long cashierId;
    private Long memberId;
    private LocalDateTime saleTime;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 构造方法
    public Sale() {
        this.status = "pending";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}