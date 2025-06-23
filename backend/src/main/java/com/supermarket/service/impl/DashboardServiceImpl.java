package com.supermarket.service.impl;

import com.supermarket.mapper.SaleMapper;
import com.supermarket.service.DashboardService;
import com.supermarket.service.MemberService;
import com.supermarket.service.ProductService;
import com.supermarket.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private MemberService memberService;
    
    @Autowired(required = false)
    private SaleService saleService;

    @Autowired
    private SaleMapper saleMapper;
    @Override
    public Map<String, Object> getTodayStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 获取今日销售统计（如果有销售服务的话）
            if (saleService != null) {

                LocalDate today = LocalDate.now();
                LocalDateTime beginTime  = LocalDateTime.of(today, LocalTime.MIN);
                LocalDateTime endTime = LocalDateTime.of(today, LocalTime.MAX);
                int todaySalesAmount = saleMapper.getTodaySalesAmount(beginTime, endTime);
                stats.put("todaySales", todaySalesAmount );
                int todayOrders = saleMapper.getTodayOrders(beginTime, endTime);
                stats.put("todayOrders", todayOrders);
                stats.put("salesChange", 8.5);
                stats.put("ordersChange", 12.3);
            } else {
                // 模拟数据
                stats.put("todaySales", 8456.30);
                stats.put("todayOrders", 67);
                stats.put("salesChange", 15.2);
                stats.put("ordersChange", 8.7);
            }
            
            System.out.println("✅ 今日统计数据生成: " + stats);
            
        } catch (Exception e) {
            System.err.println("❌ 获取今日统计失败: " + e.getMessage());
            // 返回默认值
            stats.put("todaySales", 0.0);
            stats.put("todayOrders", 0);
            stats.put("salesChange", 0.0);
            stats.put("ordersChange", 0.0);
        }
        
        return stats;
    }

    @Override
    public Map<String, Object> getSystemOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        try {
            // 获取商品总数
            long totalProducts = productService.count();
            overview.put("totalProducts", totalProducts);
            
            // 获取会员总数
            long totalMembers = memberService.count();
            overview.put("totalMembers", totalMembers);
            
            // 获取活跃会员数（模拟：总会员数的30%）
            long activeMembers = Math.round(totalMembers * 0.3);
            overview.put("activeMembers", activeMembers);
            
            // 获取低库存商品数量
            List<Map<String, Object>> lowStockProducts = getLowStockProducts(10, 100);
            overview.put("lowStockCount", lowStockProducts.size());
            
            System.out.println("✅ 系统概览数据: " + overview);
            
        } catch (Exception e) {
            System.err.println("❌ 获取系统概览失败: " + e.getMessage());
            // 返回默认值
            overview.put("totalProducts", 0);
            overview.put("totalMembers", 0);
            overview.put("activeMembers", 0);
            overview.put("lowStockCount", 0);
        }
        
        return overview;
    }

    @Override
    public List<Map<String, Object>> getLowStockProducts(Integer minStockLevel, Integer limit) {
        List<Map<String, Object>> products = new ArrayList<>();
        
        try {
            // TODO: 实现真实的低库存查询
            // 这里应该查询数据库中库存小于minStockLevel的商品
            
            // 模拟低库存商品数据
            if (minStockLevel <= 10) {
                Map<String, Object> product1 = new HashMap<>();
                product1.put("productId", 1);
                product1.put("productName", "矿泉水500ml");
                product1.put("stockQuantity", 5);
                product1.put("minStockLevel", 20);
                products.add(product1);
                
                Map<String, Object> product2 = new HashMap<>();
                product2.put("productId", 2);
                product2.put("productName", "牙刷");
                product2.put("stockQuantity", 3);
                product2.put("minStockLevel", 10);
                products.add(product2);
            }
            
            // 限制返回数量
            if (products.size() > limit) {
                products = products.subList(0, limit);
            }
            
            System.out.println("✅ 低库存商品查询完成: " + products.size() + " 个");
            
        } catch (Exception e) {
            System.err.println("❌ 获取低库存商品失败: " + e.getMessage());
        }
        
        return products;
    }

    @Override
    public List<Map<String, Object>> getRecentActivities(Integer limit) {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        try {
            // TODO: 实现真实的活动记录查询
            // 模拟最近活动数据
            for (int i = 0; i < Math.min(limit, 5); i++) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("id", i + 1);
                activity.put("type", i % 2 == 0 ? "sale" : "product");
                activity.put("description", i % 2 == 0 ? "完成销售订单" : "添加新商品");
                activity.put("time", LocalDate.now().minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                activities.add(activity);
            }
            
        } catch (Exception e) {
            System.err.println("❌ 获取最近活动失败: " + e.getMessage());
        }
        
        return activities;
    }

    @Override
    public Map<String, Object> getSalesChart(Integer days) {
        Map<String, Object> chartData = new HashMap<>();
        
        try {
            // TODO: 实现真实的销售趋势数据
            List<String> dates = new ArrayList<>();
            List<Double> sales = new ArrayList<>();
            
            // 模拟最近几天的销售数据
            for (int i = days - 1; i >= 0; i--) {
                dates.add(LocalDate.now().minusDays(i).format(DateTimeFormatter.ofPattern("MM-dd")));
                sales.add(Math.random() * 10000 + 5000); // 随机生成5000-15000的销售额
            }
            
            chartData.put("dates", dates);
            chartData.put("sales", sales);
            
        } catch (Exception e) {
            System.err.println("❌ 获取销售趋势数据失败: " + e.getMessage());
        }
        
        return chartData;
    }

    @Override
    public List<Map<String, Object>> getTopProducts(Integer limit) {
        List<Map<String, Object>> products = new ArrayList<>();
        
        try {
            // TODO: 实现真实的热销商品排行
            // 模拟热销商品数据
            String[] productNames = {"可口可乐500ml", "农夫山泉550ml", "康师傅方便面", "牙刷", "洗发水"};
            
            for (int i = 0; i < Math.min(limit, productNames.length); i++) {
                Map<String, Object> product = new HashMap<>();
                product.put("productId", i + 1);
                product.put("productName", productNames[i]);
                product.put("salesCount", 100 - i * 10); // 模拟销量递减
                product.put("revenue", (100 - i * 10) * (3.5 + i * 0.5)); // 模拟营收
                products.add(product);
            }
            
        } catch (Exception e) {
            System.err.println("❌ 获取热销商品排行失败: " + e.getMessage());
        }
        
        return products;
    }
}