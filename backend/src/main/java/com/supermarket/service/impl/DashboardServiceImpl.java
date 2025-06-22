package com.supermarket.service.impl;

import com.supermarket.service.DashboardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Override
    public Map<String, Object> getTodayStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 模拟今日销售数据
        stats.put("todaySales", new BigDecimal("12680.50"));
        stats.put("todayOrders", 89);
        stats.put("todayCustomers", 76);
        stats.put("todayProducts", 256);
        
        // 同比增长
        stats.put("salesGrowth", 8.5);
        stats.put("ordersGrowth", 12.3);
        stats.put("customersGrowth", 6.8);
        
        // 统计时间
        stats.put("updateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        return stats;
    }

    @Override
    public List<Map<String, Object>> getLowStockProducts(Integer minStockLevel, Integer limit) {
        List<Map<String, Object>> products = new ArrayList<>();
        
        // 模拟低库存商品数据，使用更真实的库存数据
        String[][] lowStockData = {
            {"可口可乐500ml", "6901028000001", "15", "50", "30.0"}, // 库存15，最低50，库存率30%
            {"农夫山泉550ml", "6902148000002", "8", "30", "26.7"},  // 库存8，最低30，库存率26.7%
            {"康师傅方便面", "6901326000003", "5", "20", "25.0"},    // 库存5，最低20，库存率25%
            {"牙刷", "6901234000004", "12", "40", "30.0"},         // 库存12，最低40，库存率30%
            {"洗发水", "6901234000005", "3", "15", "20.0"},        // 库存3，最低15，库存率20%
            {"面包", "6901234000006", "6", "25", "24.0"},          // 库存6，最低25，库存率24%
            {"酸奶", "6901234000007", "18", "60", "30.0"},         // 库存18，最低60，库存率30%
            {"饼干", "6901234000008", "4", "20", "20.0"}           // 库存4，最低20，库存率20%
        };
        
        int count = 0;
        for (String[] data : lowStockData) {
            if (count >= limit) break;
            
            int currentStock = Integer.parseInt(data[2]);
            int minStock = Integer.parseInt(data[3]);
            
            // 只返回库存低于阈值的商品
            if (currentStock <= minStockLevel && currentStock <= minStock) {
                Map<String, Object> product = new HashMap<>();
                product.put("productId", count + 1);
                product.put("productName", data[0]);
                product.put("barcode", data[1]);
                product.put("currentStock", currentStock);
                product.put("minStockLevel", minStock);
                product.put("stockRatio", Double.parseDouble(data[4]));
                
                // 库存状态
                if (currentStock == 0) {
                    product.put("stockStatus", "零库存");
                    product.put("urgencyLevel", "紧急");
                } else if (currentStock <= minStock * 0.2) {
                    product.put("stockStatus", "严重不足");
                    product.put("urgencyLevel", "高");
                } else if (currentStock <= minStock * 0.5) {
                    product.put("stockStatus", "库存不足");
                    product.put("urgencyLevel", "中");
                } else {
                    product.put("stockStatus", "偏低");
                    product.put("urgencyLevel", "低");
                }
                
                // 建议补货数量
                int suggestedRestock = Math.max(minStock * 2 - currentStock, 0);
                product.put("suggestedRestock", suggestedRestock);
                
                products.add(product);
                count++;
            }
        }
        
        // 按库存比例排序，最紧急的排在前面
        products.sort((a, b) -> {
            Double ratioA = (Double) a.get("stockRatio");
            Double ratioB = (Double) b.get("stockRatio");
            return ratioA.compareTo(ratioB);
        });
        
        return products;
    }

    @Override
    public Map<String, Object> getSystemOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        overview.put("totalProducts", 1256);
        overview.put("totalMembers", 896);
        overview.put("totalSales", new BigDecimal("458970.50"));
        overview.put("lowStockAlerts", 8);
        overview.put("systemStatus", "正常");
        overview.put("onlineUsers", 5);
        
        return overview;
    }

    @Override
    public List<Map<String, Object>> getRecentActivities(Integer limit) {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        String[] activityTypes = {"销售", "进货", "会员注册", "库存预警", "用户登录"};
        String[] descriptions = {
            "收银员张三完成了一笔¥156.80的交易",
            "商品管理员李四登记了一批新进货",
            "新会员王五注册成功",
            "商品'可口可乐500ml'库存不足",
            "系统管理员admin登录系统"
        };
        
        for (int i = 0; i < Math.min(limit, descriptions.length); i++) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("id", i + 1);
            activity.put("type", activityTypes[i]);
            activity.put("description", descriptions[i]);
            activity.put("time", LocalDateTime.now().minusMinutes(i * 15)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            activity.put("operator", i % 2 == 0 ? "张三" : "李四");
            
            activities.add(activity);
        }
        
        return activities;
    }

    @Override
    public Map<String, Object> getSalesChart(Integer days) {
        Map<String, Object> chartData = new HashMap<>();
        
        List<String> dates = new ArrayList<>();
        List<BigDecimal> sales = new ArrayList<>();
        
        LocalDate today = LocalDate.now();
        Random random = new Random();
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
            
            // 模拟销售额：基础金额 + 随机波动
            double baseSales = 8000 + random.nextGaussian() * 2000;
            sales.add(new BigDecimal(Math.max(baseSales, 1000)).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        
        chartData.put("dates", dates);
        chartData.put("sales", sales);
        chartData.put("period", days + "天");
        
        return chartData;
    }

    @Override
    public List<Map<String, Object>> getTopProducts(Integer limit) {
        List<Map<String, Object>> products = new ArrayList<>();
        
        String[][] topProductData = {
            {"可口可乐500ml", "380", "1330.00"},
            {"农夫山泉550ml", "295", "737.50"},
            {"康师傅方便面", "268", "1206.00"},
            {"奥利奥饼干", "242", "1938.00"},
            {"旺旺雪饼", "189", "945.00"},
            {"统一绿茶", "167", "500.10"},
            {"德芙巧克力", "156", "2496.00"},
            {"三只松鼠坚果", "145", "2900.00"},
            {"蒙牛纯牛奶", "134", "804.00"},
            {"立白洗衣粉", "128", "1024.00"}
        };
        
        for (int i = 0; i < Math.min(limit, topProductData.length); i++) {
            Map<String, Object> product = new HashMap<>();
            product.put("rank", i + 1);
            product.put("productName", topProductData[i][0]);
            product.put("salesQuantity", Integer.parseInt(topProductData[i][1]));
            product.put("salesAmount", new BigDecimal(topProductData[i][2]));
            product.put("trend", i % 3 == 0 ? "上升" : (i % 3 == 1 ? "下降" : "持平"));
            
            products.add(product);
        }
        
        return products;
    }
}