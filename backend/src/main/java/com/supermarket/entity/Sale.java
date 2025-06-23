package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 销售记录实体类
 */
@Data
@TableName("sales")
public class Sale {
    @TableId(type = IdType.AUTO)
    private Long saleId;
    
    private String saleNumber;        // 订单号
    
    private Long memberId;            // 会员ID（可为空）
    
    private Long cashierId;           // 收银员ID
    
    private Double totalAmount;       // 商品总金额
    
    private Double discountAmount;    // 优惠金额（包括会员优惠）
    
    private Double finalAmount;       // 实收金额
    
    private String paymentMethod;     // 支付方式

    private String status;            // 状态：completed, cancelled, refunded
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime saleDate;   // 销售时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    public Sale() {
        this.status = "completed";
        this.saleDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}