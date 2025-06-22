package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("purchase_items")
public class PurchaseItem {
    @TableId(type = IdType.AUTO)
    private Long itemId;
    
    private Long purchaseId;
    private Long productId;
    private Integer quantity;
    private BigDecimal costPrice;
    private BigDecimal totalCost;
    
    // 关联字段
    @TableField(exist = false)
    private String productName;
    
    @TableField(exist = false)
    private String barcode;
}
