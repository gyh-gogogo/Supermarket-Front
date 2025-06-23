package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("products")
public class Product {
    
    @TableId(type = IdType.AUTO)
    private Long productId;

    private String productName;
    
    private String barcode;
    
    private BigDecimal price;
    
    private BigDecimal costPrice;
    
    private Integer stockQuantity;
    
    private Integer minStockLevel;
    
    private String description;
    
    private Long categoryId;  // 分类ID字段
    
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 非数据库字段，用于返回分类名称
    @TableField(exist = false)
    private String categoryName;
    
    public Product() {
        this.status = "active";
        this.stockQuantity = 0;
        this.minStockLevel = 10;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}