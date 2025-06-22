package com.supermarket.controller;

import com.supermarket.entity.Sale;
import com.supermarket.entity.SaleItem;
import com.supermarket.service.SaleService;
import com.supermarket.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 销售记录控制器
 */
@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SaleController {

    @Autowired
    private SaleService saleService;

    /**
     * 获取销售记录列表
     */
    @GetMapping
    public Result<List<Sale>> getAllSales(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) Long cashierId) {
        try {
            List<Sale> sales = saleService.getSales(startDate, endDate, orderNumber, cashierId);
            return Result.success(sales);
        } catch (Exception e) {
            return Result.error("获取销售记录失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取销售记录详情
     */
    @GetMapping("/{id}")
    public Result<Sale> getSaleById(@PathVariable Long id) {
        try {
            Sale sale = saleService.getSaleById(id);
            if (sale != null) {
                return Result.success(sale);
            } else {
                return Result.error("销售记录不存在");
            }
        } catch (Exception e) {
            return Result.error("获取销售记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取销售明细
     */
    @GetMapping("/{id}/items")
    public Result<List<Map<String, Object>>> getSaleItems(@PathVariable Long id) {
        try {
            List<Map<String, Object>> items = saleService.getSaleItems(id);
            return Result.success(items);
        } catch (Exception e) {
            return Result.error("获取销售明细失败: " + e.getMessage());
        }
    }

    /**
     * 创建销售记录
     */
    @PostMapping
    public Result<Sale> createSale(@RequestBody Map<String, Object> saleData) {
        try {
            Sale sale = saleService.createSale(saleData);
            return Result.success(sale);
        } catch (Exception e) {
            return Result.error("创建销售记录失败: " + e.getMessage());
        }
    }

    /**
     * 取消销售记录
     */
    @PutMapping("/{id}/cancel")
    public Result<String> cancelSale(@PathVariable Long id) {
        try {
            boolean success = saleService.cancelSale(id);
            if (success) {
                return Result.success("销售记录已取消");
            } else {
                return Result.error("取消销售记录失败");
            }
        } catch (Exception e) {
            return Result.error("取消销售记录失败: " + e.getMessage());
        }
    }

    /**
     * 退货处理
     */
    @PostMapping("/{id}/refund")
    public Result<String> refundSale(@PathVariable Long id, @RequestBody Map<String, Object> refundData) {
        try {
            boolean success = saleService.refundSale(id, refundData);
            if (success) {
                return Result.success("退货处理成功");
            } else {
                return Result.error("退货处理失败");
            }
        } catch (Exception e) {
            return Result.error("退货处理失败: " + e.getMessage());
        }
    }

    /**
     * 获取今日销售统计
     */
    @GetMapping("/today-stats")
    public Result<Map<String, Object>> getTodayStats() {
        try {
            Map<String, Object> stats = saleService.getTodayStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取今日销售统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取销售趋势数据
     */
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> getSalesTrend(
            @RequestParam(defaultValue = "week") String period) {
        try {
            List<Map<String, Object>> trend = saleService.getSalesTrend(period);
            return Result.success(trend);
        } catch (Exception e) {
            return Result.error("获取销售趋势失败: " + e.getMessage());
        }
    }

    /**
     * 获取收银员销售排行
     */
    @GetMapping("/cashier-ranking")
    public Result<List<Map<String, Object>>> getCashierRanking(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            List<Map<String, Object>> ranking = saleService.getCashierRanking(startDate, endDate);
            return Result.success(ranking);
        } catch (Exception e) {
            return Result.error("获取收银员排行失败: " + e.getMessage());
        }
    }

    /**
     * 获取商品销售排行
     */
    @GetMapping("/product-ranking")
    public Result<List<Map<String, Object>>> getProductRanking(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Map<String, Object>> ranking = saleService.getProductRanking(startDate, endDate, limit);
            return Result.success(ranking);
        } catch (Exception e) {
            return Result.error("获取商品销售排行失败: " + e.getMessage());
        }
    }

    /**
     * 获取销售报表数据
     */
    @GetMapping("/report")
    public Result<Map<String, Object>> getSalesReport(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "day") String groupBy) {
        try {
            Map<String, Object> report = saleService.getSalesReport(startDate, endDate, groupBy);
            return Result.success(report);
        } catch (Exception e) {
            return Result.error("获取销售报表失败: " + e.getMessage());
        }
    }
}