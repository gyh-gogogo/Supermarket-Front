-- 创建销售订单表（修正版）
DROP TABLE IF EXISTS sale_items;
DROP TABLE IF EXISTS sales;

CREATE TABLE IF NOT EXISTS sales (
    sale_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '销售ID',
    sale_number VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    member_id BIGINT DEFAULT NULL COMMENT '会员ID',
    cashier_id BIGINT NOT NULL COMMENT '收银员ID',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '商品总金额',
    discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额（包括会员优惠）',
    final_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '实收金额',
    payment_method VARCHAR(20) NOT NULL COMMENT '支付方式',
    received_amount DECIMAL(10,2) DEFAULT NULL COMMENT '实收现金',
    change_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '找零金额',
    status ENUM('completed', 'cancelled', 'refunded') DEFAULT 'completed' COMMENT '状态',
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '销售时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_sale_number (sale_number),
    INDEX idx_member_id (member_id),
    INDEX idx_cashier_id (cashier_id),
    INDEX idx_sale_date (sale_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单表';

CREATE TABLE IF NOT EXISTS sale_items (
    sale_item_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '销售明细ID',
    sale_id BIGINT NOT NULL COMMENT '销售订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    barcode VARCHAR(50) COMMENT '商品条码',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    quantity INT NOT NULL COMMENT '数量',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    
    INDEX idx_sale_id (sale_id),
    INDEX idx_product_id (product_id),
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售明细表';
