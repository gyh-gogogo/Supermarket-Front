@echo off
title 超市管理系统后端启动
color 0A
echo ================================
echo   超市管理系统后端启动脚本
echo ================================
echo.

echo 🔧 正在检查后端环境...
echo.

echo [1/5] 检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java环境未配置，请安装JDK 17+
    pause
    exit /b 1
) else (
    echo ✅ Java环境检查通过
    java -version
)

echo.
echo [2/5] 检查Maven环境...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven环境未配置，请安装Maven 3.6+
    pause
    exit /b 1
) else (
    echo ✅ Maven环境检查通过
)

echo.
echo [3/5] 进入后端目录...
cd backend
if %errorlevel% neq 0 (
    echo ❌ 后端目录不存在
    pause
    exit /b 1
)

echo.
echo [4/5] 检查pom.xml文件...
if not exist "pom.xml" (
    echo ❌ pom.xml文件不存在
    pause
    exit /b 1
) else (
    echo ✅ pom.xml文件存在
)

echo.
echo [5/5] 启动Spring Boot应用...
echo 🚀 正在启动超市管理系统后端服务...
echo.

start "超市管理系统后端" cmd /k "title 后端服务器 && echo. && echo 🏪 超市管理系统后端服务器 && echo. && echo 📍 服务地址: && echo   - API: http://localhost:8080/api && echo   - 测试: http://localhost:8080/api/test/hello && echo. && echo 🔧 开发模式: 已激活 && echo 📊 数据库: MySQL (localhost:3306/supermarket_db) && echo. && echo 按 Ctrl+C 停止服务器 && echo. && mvn spring-boot:run"

cd ..

echo 等待后端服务启动...
timeout /t 10 /nobreak >nul

echo.
echo ================================
echo 🎉 后端启动完成！
echo ================================
echo.

echo ✅ 启动信息：
echo - 🌐 后端API地址: http://localhost:8080/api
echo - 🧪 健康检查: http://localhost:8080/api/test/hello
echo - 📊 数据库: MySQL localhost:3306/supermarket_db
echo - 🔧 开发模式: 热重载已启用
echo.

echo 🔍 API接口列表：
echo - GET  /api/test/hello           - 测试接口
echo - GET  /api/dashboard/today-stats - 今日统计
echo - GET  /api/members              - 会员列表
echo - GET  /api/products/page        - 商品分页
echo - GET  /api/categories/page      - 分类分页
echo - GET  /api/purchases/page       - 进货记录
echo.

echo 💡 开发提示：
echo 1. 修改Java文件后自动重启
echo 2. API接口支持跨域访问
echo 3. 数据库连接配置在application.yml
echo 4. 日志输出在控制台
echo.

echo 🌐 正在测试API连接...
timeout /t 3 /nobreak >nul
start http://localhost:8080/api/test/hello

pause