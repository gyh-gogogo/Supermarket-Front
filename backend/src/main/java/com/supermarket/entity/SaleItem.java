package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 销售明细实体类
 */
@Data
@TableName("sale_items")
public class SaleItem {
    
    @TableId(type = IdType.AUTO)
    private Long saleItemId;
    
    private Long saleId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;

    // 构造方法
    public SaleItem() {
        this.quantity = 1;
    }
}