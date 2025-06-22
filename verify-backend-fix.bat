@echo off
title 后端编译错误修复验证
color 0A
echo ================================
echo    后端编译错误修复验证
echo ================================
echo.

echo 🔧 正在验证Mapper接口修复...
echo.

echo [1/4] 检查SaleItemMapper继承BaseMapper...
findstr /C:"extends BaseMapper<SaleItem>" "backend\src\main\java\com\supermarket\mapper\SaleItemMapper.java" >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ SaleItemMapper继承BaseMapper - 已修复
) else (
    echo ❌ SaleItemMapper继承BaseMapper - 需要修复
)

echo.
echo [2/4] 检查SaleMapper继承BaseMapper...
findstr /C:"extends BaseMapper<Sale>" "backend\src\main\java\com\supermarket\mapper\SaleMapper.java" >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ SaleMapper继承BaseMapper - 已修复
) else (
    echo ❌ SaleMapper继承BaseMapper - 需要修复
)

echo.
echo [3/4] 检查MemberMapper继承BaseMapper...
findstr /C:"extends BaseMapper<Member>" "backend\src\main\java\com\supermarket\mapper\MemberMapper.java" >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ MemberMapper继承BaseMapper - 已修复
) else (
    echo ❌ MemberMapper继承BaseMapper - 需要修复
)

echo.
echo [4/4] 执行编译测试...
cd backend
echo 正在清理和编译...
mvn clean compile -q
if %errorlevel% equ 0 (
    echo ✅ 编译成功 - 所有错误已修复
) else (
    echo ❌ 编译失败 - 还有错误需要修复
    echo.
    echo 重新执行详细编译以查看错误：
    mvn clean compile
)

cd ..

echo.
echo ================================
echo 🎉 修复验证完成！
echo ================================
echo.

echo 📋 修复摘要：
echo - SaleItemMapper: 继承BaseMapper接口
echo - SaleMapper: 继承BaseMapper接口  
echo - MemberMapper: 继承BaseMapper接口
echo - ServiceImpl: 正确的泛型类型参数
echo - BigDecimal: 使用现代RoundingMode
echo.

pause