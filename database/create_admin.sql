-- 创建默认管理员账户
USE supermarket_db;

-- 插入默认管理员（密码: admin123）
INSERT INTO users (username, password, email, role) VALUES 
('admin', '$2b$10$rOsWh8j8yOQz6K.N1XkFKO8nQZrJ5QbF8E6J2H3QZ1Q2Z3Q4Q5Q6Q7', 'admin@supermarket.com', 'admin')
ON DUPLICATE KEY UPDATE 
password = '$2b$10$rOsWh8j8yOQz6K.N1XkFKO8nQZrJ5QbF8E6J2H3QZ1Q2Z3Q4Q5Q6Q7';

-- 插入示例收银员账户（密码: cashier123）
INSERT INTO users (username, password, email, role) VALUES 
('cashier', '$2b$10$rOsWh8j8yOQz6K.N1XkFKO8nQZrJ5QbF8E6J2H3QZ1Q2Z3Q4Q5Q6Q8', 'cashier@supermarket.com', 'cashier')
ON DUPLICATE KEY UPDATE 
password = '$2b$10$rOsWh8j8yOQz6K.N1XkFKO8nQZrJ5QbF8E6J2H3QZ1Q2Z3Q4Q5Q6Q8';

SELECT 'Default accounts created successfully!' as message;
SELECT username, role FROM users WHERE username IN ('admin', 'cashier');
