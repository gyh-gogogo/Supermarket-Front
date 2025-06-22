-- 超市管理系统数据库表结构
-- 创建数据库
CREATE DATABASE IF NOT EXISTS supermarket_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE supermarket_db;

-- 会员表
CREATE TABLE members (
    member_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会员ID',
    member_name VARCHAR(50) NOT NULL COMMENT '会员姓名',
    phone_number VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号码',
    id_card VARCHAR(18) COMMENT '身份证号',
    birthday DATE COMMENT '生日',
    gender ENUM('male', 'female') DEFAULT 'male' COMMENT '性别',
    member_level ENUM('bronze', 'silver', 'gold', 'diamond') DEFAULT 'bronze' COMMENT '会员等级',
    points INT DEFAULT 0 COMMENT '积分',
    total_spent DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计消费',
    registered_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    last_visit DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后访问时间',
    remarks TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '会员表';

-- 商品表
CREATE TABLE products (
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
    barcode VARCHAR(50) NOT NULL UNIQUE COMMENT '商品条码',
    price DECIMAL(10,2) NOT NULL COMMENT '售价',
    cost_price DECIMAL(10,2) DEFAULT 0.00 COMMENT '成本价',
    stock_quantity INT DEFAULT 0 COMMENT '库存数量',
    min_stock_level INT DEFAULT 10 COMMENT '最低库存',
    description TEXT COMMENT '商品描述',
    status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '商品表';

-- 用户表（收银员等）
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    role ENUM('admin', 'cashier', 'manager') DEFAULT 'cashier' COMMENT '角色',
    phone VARCHAR(20) COMMENT '电话',
    email VARCHAR(100) COMMENT '邮箱',
    status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户表';

-- 销售记录表
CREATE TABLE sales (
    sale_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '销售ID',
    order_number VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '商品总额',
    discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
    final_amount DECIMAL(10,2) NOT NULL COMMENT '实收金额',
    payment_method ENUM('cash', 'card', 'alipay', 'wechat') DEFAULT 'cash' COMMENT '支付方式',
    cashier_id BIGINT NOT NULL COMMENT '收银员ID',
    member_id BIGINT COMMENT '会员ID',
    sale_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '销售时间',
    status ENUM('completed', 'cancelled', 'refunded') DEFAULT 'completed' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (cashier_id) REFERENCES users(user_id),
    FOREIGN KEY (member_id) REFERENCES members(member_id)
) COMMENT '销售记录表';

-- 销售明细表
CREATE TABLE sale_items (
    sale_item_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '销售明细ID',
    sale_id BIGINT NOT NULL COMMENT '销售ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '数量',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计',
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
) COMMENT '销售明细表';

-- 创建索引
CREATE INDEX idx_members_phone ON members(phone_number);
CREATE INDEX idx_members_level ON members(member_level);
CREATE INDEX idx_members_registered ON members(registered_at);

CREATE INDEX idx_products_barcode ON products(barcode);
CREATE INDEX idx_products_name ON products(product_name);
CREATE INDEX idx_products_status ON products(status);

CREATE INDEX idx_sales_time ON sales(sale_time);
CREATE INDEX idx_sales_cashier ON sales(cashier_id);
CREATE INDEX idx_sales_member ON sales(member_id);
CREATE INDEX idx_sales_status ON sales(status);

CREATE INDEX idx_sale_items_sale ON sale_items(sale_id);
CREATE INDEX idx_sale_items_product ON sale_items(product_id);

-- 插入示例数据
-- 插入用户（收银员）
INSERT INTO users (username, password, real_name, role, phone) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '管理员', 'admin', '13800000000'),
('cashier1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '张小明', 'cashier', '13811111111'),
('cashier2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '李小红', 'cashier', '13822222222');

-- 插入会员数据
INSERT INTO members (member_name, phone_number, member_level, points, total_spent) VALUES
('张三', '13812345678', 'gold', 2580, 15680.50),
('李四', '13987654321', 'silver', 1250, 8960.30),
('王五', '13666666666', 'bronze', 680, 3420.80),
('赵六', '13777777777', 'diamond', 5200, 28960.00),
('钱七', '13888888888', 'silver', 1580, 9850.60);

-- 插入商品数据
INSERT INTO products (product_name, barcode, price, cost_price, stock_quantity, min_stock_level, description) VALUES
('可口可乐500ml', '6901028000001', 3.50, 2.80, 85, 20, '经典可乐，500ml装'),
('农夫山泉550ml', '6902148000002', 2.50, 1.80, 120, 30, '天然饮用水，550ml装'),
('康师傅方便面', '6901326000003', 4.50, 3.20, 156, 50, '红烧牛肉面'),
('旺旺雪饼', '6901326000004', 6.80, 4.50, 89, 25, '香脆雪饼，原味'),
('统一绿茶', '6901326000005', 3.00, 2.10, 78, 20, '无糖绿茶饮料'),
('奥利奥饼干', '6901326000006', 12.50, 8.80, 45, 15, '经典奥利奥饼干'),
('德芙巧克力', '6901326000007', 25.80, 18.60, 32, 10, '丝滑牛奶巧克力'),
('怡宝纯净水', '6901326000008', 1.50, 1.00, 200, 50, '纯净饮用水，500ml'),
('薯片', '6901326000009', 8.50, 5.90, 67, 20, '香脆薯片，番茄味'),
('牛奶', '6901326000010', 15.80, 12.50, 43, 15, '纯牛奶，250ml*6盒');

-- 插入销售记录示例
INSERT INTO sales (order_number, total_amount, discount_amount, final_amount, payment_method, cashier_id, member_id) VALUES
('ORD202401150001', 28.50, 1.50, 27.00, 'cash', 2, 1),
('ORD202401150002', 15.60, 0.00, 15.60, 'alipay', 2, NULL),
('ORD202401150003', 42.80, 2.80, 40.00, 'wechat', 3, 2);

-- 插入销售明细示例
INSERT INTO sale_items (sale_id, product_id, quantity, price, subtotal) VALUES
-- 第一单
(1, 1, 2, 3.50, 7.00),
(1, 2, 3, 2.50, 7.50),
(1, 3, 3, 4.50, 13.50),
-- 第二单
(2, 4, 1, 6.80, 6.80),
(2, 5, 2, 3.00, 6.00),
(2, 8, 2, 1.50, 3.00),
-- 第三单
(3, 6, 1, 12.50, 12.50),
(3, 7, 1, 25.80, 25.80),
(3, 9, 1, 8.50, 8.50);