package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 销售明细实体类
 */
@Data
@TableName("sale_items")
public class SaleItem {
    @TableId(type = IdType.AUTO)
    private Long saleItemId;
    
    private Long saleId;              // 关联的销售订单ID
    
    private Long productId;           // 商品ID
    
    private String productName;       // 商品名称
    
    private String barcode;           // 商品条码
    
    private Double unitPrice;         // 单价
    
    private Integer quantity;         // 数量

    @TableField("total_price")
    private Double subtotal;          // 小计金额
    
    public SaleItem() {}
    
    public SaleItem(Long saleId, Long productId, String productName, String barcode, 
                   Double unitPrice, Integer quantity, Double subtotal) {
        this.saleId = saleId;
        this.productId = productId;
        this.productName = productName;
        this.barcode = barcode;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }
}