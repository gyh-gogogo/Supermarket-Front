# 超市管理系统

## 🚀 快速开始

### 第一步：环境检查
```bash
# 双击运行环境检查
check-environment.bat
```

### 第二步：安装依赖
```bash
# 双击运行依赖安装
install-dependencies.bat
```

### 第三步：创建数据库
1. 启动MySQL服务
2. 使用MySQL客户端执行：
```sql
-- 创建数据库和表结构
source database/supermarket.sql

-- 创建默认账户
source database/create_admin.sql
```

### 第四步：配置数据库
编辑 `backend\.env` 文件：
```env
DB_PASSWORD=你的MySQL密码
```

### 第五步：启动项目
```bash
# 双击运行启动脚本
start.bat
```

## 📋 默认账户
- **管理员**: `admin` / `admin123`
- **收银员**: `cashier` / `cashier123`

## 🌐 访问地址
- **前端**: http://localhost:3000
- **后端**: http://localhost:5000

## 🛠️ 故障排除

### 依赖安装问题
```bash
# 如果npm安装慢，使用淘宝镜像
npm config set registry https://registry.npmmirror.com/
```

### TypeScript错误
确保依赖已正确安装：
```bash
cd backend && npm install
cd frontend && npm install
```

### 数据库连接问题
1. 检查MySQL服务是否启动
2. 检查 `backend\.env` 中的数据库配置
3. 确保数据库 `supermarket_db` 已创建

### 端口占用
如果端口被占用，修改配置：
- 前端端口：`frontend/vite.config.ts`
- 后端端口：`backend/.env`

## 📁 项目结构
```
Supermarket Front/
├── frontend/              # Vue.js 前端
│   ├── src/
│   ├── package.json
│   └── vite.config.ts
├── backend/               # Node.js 后端
│   ├── src/
│   ├── package.json
│   └── .env
├── database/              # 数据库脚本
│   ├── supermarket.sql
│   └── create_admin.sql
├── check-environment.bat  # 环境检查
├── install-dependencies.bat # 依赖安装
├── start.bat             # 项目启动
└── README.md             # 项目说明
```

## 🔧 开发命令

### 前端开发
```bash
cd frontend
npm run dev    # 开发服务器
npm run build  # 生产构建
```

### 后端开发  
```bash
cd backend
npm run dev    # 开发服务器 
npm run build  # 生产构建
npm start      # 生产启动
```
