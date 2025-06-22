package com.supermarket.controller;

import com.supermarket.common.Result;
import com.supermarket.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Dashboard仪表盘控制器
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取今日统计数据
     */
    @GetMapping("/today-stats")
    public Result<Map<String, Object>> getTodayStats() {
        try {
            System.out.println("📊 获取今日销售统计");
            Map<String, Object> stats = dashboardService.getTodayStats();
            return Result.success(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取今日统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取低库存商品列表 - 修复版本
     */
    @GetMapping("/low-stock")
    public Result<List<Map<String, Object>>> getLowStockProducts(
            @RequestParam(defaultValue = "20") Integer minStockLevel,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            System.out.println("⚠️ 获取低库存商品，最低库存阈值: " + minStockLevel + ", 限制数量: " + limit);
            
            // 参数验证
            if (minStockLevel <= 0) {
                return Result.error("最低库存阈值必须大于0");
            }
            if (limit <= 0 || limit > 100) {
                return Result.error("限制数量必须在1-100之间");
            }
            
            List<Map<String, Object>> products = dashboardService.getLowStockProducts(minStockLevel, limit);
            
            System.out.println("✅ 查询到 " + products.size() + " 个低库存商品");
            
            // 根据数据量返回不同的消息
            String message;
            if (products.isEmpty()) {
                message = "暂无低库存商品";
            } else if (products.size() >= limit) {
                message = "查询到" + products.size() + "个低库存商品（已达到查询上限）";
            } else {
                message = "查询到" + products.size() + "个低库存商品";
            }
            
            return Result.success(products);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ 获取低库存商品失败: " + e.getMessage());
            return Result.error("获取低库存商品失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统概览数据
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getSystemOverview() {
        try {
            System.out.println("📋 获取系统概览数据");
            Map<String, Object> overview = dashboardService.getSystemOverview();
            return Result.success(overview);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取系统概览失败: " + e.getMessage());
        }
    }

    /**
     * 获取最近活动记录
     */
    @GetMapping("/recent-activities")
    public Result<List<Map<String, Object>>> getRecentActivities(
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            System.out.println("🕒 获取最近活动记录，限制数量: " + limit);
            
            if (limit <= 0 || limit > 50) {
                return Result.error("活动记录数量必须在1-50之间");
            }
            
            List<Map<String, Object>> activities = dashboardService.getRecentActivities(limit);
            return Result.success(activities);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取最近活动记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取销售趋势图表数据
     */
    @GetMapping("/sales-chart")
    public Result<Map<String, Object>> getSalesChart(
            @RequestParam(defaultValue = "7") Integer days) {
        try {
            System.out.println("📈 获取销售趋势图表数据，天数: " + days);
            
            if (days <= 0 || days > 365) {
                return Result.error("查询天数必须在1-365之间");
            }
            
            Map<String, Object> chartData = dashboardService.getSalesChart(days);
            return Result.success(chartData);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取销售趋势数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取热销商品排行
     */
    @GetMapping("/top-products")
    public Result<List<Map<String, Object>>> getTopProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            System.out.println("🏆 获取热销商品排行，限制数量: " + limit);
            
            if (limit <= 0 || limit > 50) {
                return Result.error("商品数量必须在1-50之间");
            }
            
            List<Map<String, Object>> products = dashboardService.getTopProducts(limit);
            return Result.success(products);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取热销商品排行失败: " + e.getMessage());
        }
    }
}