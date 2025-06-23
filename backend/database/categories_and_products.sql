-- 创建分类表
CREATE TABLE IF NOT EXISTS categories (
    category_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY unique_category_name (category_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 确保商品表有分类字段
ALTER TABLE products ADD COLUMN IF NOT EXISTS category_id BIGINT DEFAULT NULL COMMENT '分类ID';
ALTER TABLE products ADD INDEX IF NOT EXISTS idx_category_id (category_id);

-- 插入默认分类数据
INSERT INTO categories (category_name, description, status) VALUES
('食品饮料', '各类食品和饮料', 'active'),
('日用百货', '日常生活用品', 'active'),
('服装鞋帽', '服装和鞋帽类商品', 'active'),
('图书文具', '图书和文具用品', 'active'),
('电子产品', '电子设备和配件', 'active')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- 为现有商品分配分类（示例）
UPDATE products SET category_id = 1 WHERE product_name LIKE '%可乐%' OR product_name LIKE '%水%';
UPDATE products SET category_id = 2 WHERE product_name LIKE '%牙刷%' OR product_name LIKE '%洗%';
UPDATE products SET category_id = 4 WHERE product_name LIKE '%笔%' OR product_name LIKE '%本%';
