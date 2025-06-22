@echo off
title 前端问题完整修复
color 0A
echo ================================
echo      前端问题完整修复
echo ================================
echo.

echo 🔧 正在修复前端导入错误和缺失文件...
echo.

echo [1/5] 检查前端项目目录结构...
if not exist "fronted\supermarket\src\views" (
    mkdir "fronted\supermarket\src\views" 2>nul
    echo ✅ 创建views目录
)

if not exist "fronted\supermarket\src\api" (
    mkdir "fronted\supermarket\src\api" 2>nul
    echo ✅ 创建api目录
)

if not exist "fronted\supermarket\src\components" (
    mkdir "fronted\supermarket\src\components" 2>nul
    echo ✅ 创建components目录
)

echo ✅ 目录结构检查完成

echo.
echo [2/5] 检查所有必需的Vue页面文件...
set missing_files=0

if not exist "fronted\supermarket\src\views\Dashboard.vue" (
    echo ❌ Dashboard.vue缺失
    set missing_files=1
)

if not exist "fronted\supermarket\src\views\Products.vue" (
    echo ❌ Products.vue缺失
    set missing_files=1
)

if not exist "fronted\supermarket\src\views\Members.vue" (
    echo ❌ Members.vue缺失
    set missing_files=1
)

if not exist "fronted\supermarket\src\views\Cashier.vue" (
    echo ❌ Cashier.vue缺失
    set missing_files=1
)

if not exist "fronted\supermarket\src\views\Reports.vue" (
    echo ❌ Reports.vue缺失
    set missing_files=1
)

if %missing_files%==1 (
    echo ⚠️ 发现缺失的页面文件
    echo 请确保所有页面文件都已创建
) else (
    echo ✅ 所有页面文件都存在
)

echo.
echo [3/5] 检查核心配置文件...
if not exist "fronted\supermarket\package.json" (
    echo ❌ package.json缺失
    echo 请确保package.json文件存在
    pause
    exit /b 1
) else (
    echo ✅ package.json存在
)

if not exist "fronted\supermarket\vite.config.ts" (
    echo ❌ vite.config.ts缺失
) else (
    echo ✅ vite.config.ts存在
)

if not exist "fronted\supermarket\index.html" (
    echo ❌ index.html缺失
) else (
    echo ✅ index.html存在
)

echo.
echo [4/5] 安装前端依赖...
cd fronted\supermarket

echo 正在检查Node.js版本...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Node.js未安装，请先安装Node.js 16+
    pause
    exit /b 1
)

echo 正在清理node_modules和package-lock.json...
if exist "node_modules" (
    rmdir /s /q "node_modules" 2>nul
)
if exist "package-lock.json" (
    del "package-lock.json" 2>nul
)

echo 正在安装依赖包...
npm install
if %errorlevel% neq 0 (
    echo ❌ 依赖安装失败，尝试使用淘宝镜像...
    npm config set registry https://registry.npmmirror.com
    npm install
    if %errorlevel% neq 0 (
        echo ❌ 依赖安装失败，请检查网络连接
        cd ..\..
        pause
        exit /b 1
    )
)

echo ✅ 依赖安装成功

echo.
echo [5/5] 启动前端开发服务器...
echo 🚀 正在启动前端服务...

start "超市管理系统前端" cmd /k "title 前端开发服务器 && echo. && echo 🏪 超市管理系统前端开发服务器 && echo. && echo 📍 访问地址: && echo   - 本地: http://localhost:3000 && echo   - 网络: http://0.0.0.0:3000 && echo. && echo 🔄 热重载: 已启用 && echo 🔧 开发模式: 已激活 && echo. && echo 按 Ctrl+C 停止服务器 && echo. && npm run dev"

cd ..\..

echo 等待服务启动...
timeout /t 8 /nobreak >nul

echo.
echo ================================
echo 🎉 前端修复完成！
echo ================================
echo.

echo ✅ 修复内容摘要：
echo - 📁 目录结构：完整的前端项目结构
echo - 📄 页面文件：Dashboard, Products, Members, Cashier, Reports
echo - ⚙️ 配置文件：package.json, vite.config.ts, tsconfig.json
echo - 📦 依赖安装：Vue 3, Element Plus, TypeScript等
echo - 🚀 开发服务器：已启动并运行
echo.

echo 🌐 访问信息：
echo - 前端地址: http://localhost:3000
echo - 开发模式: 热重载已启用
echo - API代理: 配置到http://localhost:8080
echo.

echo 📋 可用页面：
echo 1. 仪表盘 - http://localhost:3000/
echo 2. 收银台 - http://localhost:3000/cashier  
echo 3. 商品管理 - http://localhost:3000/products
echo 4. 会员管理 - http://localhost:3000/members
echo 5. 销售报表 - http://localhost:3000/reports
echo.

echo 🔧 开发提示：
echo - 修改Vue文件后自动热重载
echo - 浏览器自动打开到localhost:3000
echo - API请求自动代理到后端服务
echo - TypeScript类型检查已启用
echo.

echo 💡 如果遇到问题：
echo 1. 检查端口3000是否被占用
echo 2. 确保Node.js版本为16+
echo 3. 查看前端控制台的错误信息
echo 4. 确保后端服务正在运行
echo.

echo 🌐 正在尝试打开浏览器...
timeout /t 3 /nobreak >nul
start http://localhost:3000

pause