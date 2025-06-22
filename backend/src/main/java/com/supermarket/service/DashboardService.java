package com.supermarket.service;

import java.util.List;
import java.util.Map;

/**
 * Dashboard仪表盘服务接口
 */
public interface DashboardService {
    
    /**
     * 获取今日销售统计
     */
    Map<String, Object> getTodayStats();
    
    /**
     * 获取低库存商品列表 - 修复版本
     * @param minStockLevel 最低库存阈值，库存低于此值的商品将被返回
     * @param limit 返回数量限制
     */
    List<Map<String, Object>> getLowStockProducts(Integer minStockLevel, Integer limit);
    
    /**
     * 获取系统概览数据
     */
    Map<String, Object> getSystemOverview();
    
    /**
     * 获取最近活动记录
     * @param limit 返回数量限制
     */
    List<Map<String, Object>> getRecentActivities(Integer limit);
    
    /**
     * 获取销售趋势图表数据
     * @param days 查询天数
     */
    Map<String, Object> getSalesChart(Integer days);
    
    /**
     * 获取热销商品排行
     * @param limit 返回数量限制
     */
    List<Map<String, Object>> getTopProducts(Integer limit);
}