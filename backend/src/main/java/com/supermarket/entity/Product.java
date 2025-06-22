package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    
    private String status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}