package com.supermarket.controller;

import com.supermarket.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public Map<String, Object> getTodayStats() {
        try {
            Map<String, Object> result = dashboardService.getTodayStats();
            return Map.of(
                "success", true,
                "data", result,
                "message", "获取今日统计成功"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of(
                "success", false,
                "message", "获取今日统计失败: " + e.getMessage()
            );
        }
    }

    /**
     * 获取系统概览数据
     */
    @GetMapping("/overview")
    public Map<String, Object> getSystemOverview() {
        try {
            Map<String, Object> result = dashboardService.getSystemOverview();
            return Map.of(
                "success", true,
                "data", result,
                "message", "获取系统概览成功"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of(
                "success", false,
                "message", "获取系统概览失败: " + e.getMessage()
            );
        }
    }

    /**
     * 获取低库存商品
     */
    @GetMapping("/low-stock")
    public Map<String, Object> getLowStockProducts(
            @RequestParam(defaultValue = "5") Integer minStockLevel,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Map<String, Object>> result = dashboardService.getLowStockProducts(minStockLevel, limit);
            return Map.of(
                "success", true,
                "data", result,
                "message", "获取低库存商品成功"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of(
                "success", false,
                "message", "获取低库存商品失败: " + e.getMessage()
            );
        }
    }

    /**
     * 获取销售趋势图表数据
     */
    @GetMapping("/sales-chart")
    public Map<String, Object> getSalesChart(@RequestParam(defaultValue = "7") Integer days) {
        try {
            Map<String, Object> result = dashboardService.getSalesChart(days);
            return Map.of(
                "success", true,
                "data", result,
                "message", "获取销售趋势数据成功"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of(
                "success", false,
                "message", "获取销售趋势数据失败: " + e.getMessage()
            );
        }
    }

    /**
     * 获取热销商品排行
     */
    @GetMapping("/top-products")
    public Map<String, Object> getTopProducts(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Map<String, Object>> result = dashboardService.getTopProducts(limit);
            return Map.of(
                "success", true,
                "data", result,
                "message", "获取热销商品排行成功"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of(
                "success", false,
                "message", "获取热销商品排行失败: " + e.getMessage()
            );
        }
    }

    /**
     * 获取销售报表数据
     */
    @GetMapping("/sales-report")
    public Map<String, Object> getSalesReport() {
        try {
            Map<String, Object> result = dashboardService.getSalesReport();
            return Map.of(
                "success", true,
                "data", result,
                "message", "获取销售报表成功"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of(
                "success", false,
                "message", "获取销售报表失败: " + e.getMessage()
            );
        }
    }
}