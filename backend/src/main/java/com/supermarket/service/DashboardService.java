package com.supermarket.service;

import java.util.List;
import java.util.Map;

import static net.sf.jsqlparser.parser.feature.Feature.limit;

/**
 * Dashboard仪表盘服务接口
 */
public interface DashboardService {
    
    /**
     * 获取今日统计数据
     */
    Map<String, Object> getTodayStats();
    
    /**
     * 获取系统概览数据
     */
    Map<String, Object> getSystemOverview();
    
    /**
     * 获取低库存商品
     */
    List<Map<String, Object>> getLowStockProducts(Integer minStockLevel, Integer limit);
    
    /**
     * 获取最近活动记录
     */
    List<Map<String, Object>> getRecentActivities(Integer limit);
    
    /**
     * 获取销售趋势图表数据
     */
    Map<String, Object> getSalesChart(Integer days);


    Map<String, Object> getSalesReport();
    
    /**
     * 获取热销商品排行
     */
    List<Map<String, Object>> getTopProducts(Integer limit);
}