-- 检查products表结构
DESCRIBE products;

-- 如果没有category_id字段，添加它
ALTER TABLE products ADD COLUMN category_id BIGINT DEFAULT NULL COMMENT '商品分类ID';

-- 添加外键约束（可选）
-- ALTER TABLE products ADD CONSTRAINT fk_product_category 
-- FOREIGN KEY (category_id) REFERENCES categories(category_id);

-- 添加索引提高查询性能
ALTER TABLE products ADD INDEX idx_category_id (category_id);

-- 更新一些测试数据的分类
UPDATE products SET category_id = 1 WHERE product_name LIKE '%可乐%' OR product_name LIKE '%水%';
UPDATE products SET category_id = 2 WHERE product_name LIKE '%牙刷%' OR product_name LIKE '%洗%';
UPDATE products SET category_id = 4 WHERE product_name LIKE '%笔%' OR product_name LIKE '%本%';

-- 查看更新后的数据
SELECT p.product_id, p.product_name, p.category_id, c.category_name 
FROM products p 
LEFT JOIN categories c ON p.category_id = c.category_id 
WHERE p.status = 'active';
