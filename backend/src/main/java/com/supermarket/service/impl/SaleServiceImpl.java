package com.supermarket.service.impl;

import com.supermarket.entity.Sale;
import com.supermarket.entity.SaleItem;
import com.supermarket.mapper.SaleMapper;
import com.supermarket.mapper.SaleItemMapper;
import com.supermarket.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 销售记录服务实现类 - 直接实现接口，不继承ServiceImpl
 */
@Service
public class SaleServiceImpl implements SaleService {

    @Autowired
    private SaleMapper saleMapper;

    @Autowired
    private SaleItemMapper saleItemMapper;

    @Override
    public List<Sale> getSales(String startDate, String endDate, String orderNumber, Long cashierId) {
        // 这里可以添加复杂的查询逻辑
        // 暂时返回基础查询结果
        return new ArrayList<>();
    }

    @Override
    public Sale getSaleById(Long saleId) {
        return saleMapper.selectById(saleId);
    }

    @Override
    public List<Map<String, Object>> getSaleItems(Long saleId) {
        return saleItemMapper.getSaleItemsBySaleId(saleId);
    }

    @Override
    @Transactional
    public Sale createSale(Map<String, Object> saleData) {
        // 创建销售记录
        Sale sale = new Sale();
        sale.setOrderNumber(generateOrderNumber());
        sale.setTotalAmount((BigDecimal) saleData.get("totalAmount"));
        sale.setDiscountAmount((BigDecimal) saleData.getOrDefault("discountAmount", BigDecimal.ZERO));
        sale.setFinalAmount((BigDecimal) saleData.get("finalAmount"));
        sale.setPaymentMethod((String) saleData.get("paymentMethod"));
        sale.setCashierId((Long) saleData.get("cashierId"));
        sale.setMemberId((Long) saleData.get("memberId"));
        sale.setSaleTime(LocalDateTime.now());
        sale.setStatus("completed");

        saleMapper.insert(sale);

        // 创建销售明细
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) saleData.get("items");
        if (items != null) {
            for (Map<String, Object> itemData : items) {
                SaleItem item = new SaleItem();
                item.setSaleId(sale.getSaleId());
                item.setProductId((Long) itemData.get("productId"));
                item.setQuantity((Integer) itemData.get("quantity"));
                item.setPrice((BigDecimal) itemData.get("price"));
                item.setSubtotal((BigDecimal) itemData.get("subtotal"));
                saleItemMapper.insert(item);
            }
        }

        return sale;
    }    @Override
    public boolean cancelSale(Long saleId) {
        Sale sale = saleMapper.selectById(saleId);
        if (sale != null) {
            sale.setStatus("cancelled");
            sale.setUpdatedAt(LocalDateTime.now());
            return saleMapper.updateById(sale) > 0;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean refundSale(Long saleId, Map<String, Object> refundData) {
        Sale sale = saleMapper.selectById(saleId);
        if (sale != null) {
            sale.setStatus("refunded");
            sale.setUpdatedAt(LocalDateTime.now());
            return saleMapper.updateById(sale) > 0;
        }
        return false;
    }

    @Override
    public Map<String, Object> getTodayStats() {
        LocalDate today = LocalDate.now();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSales", saleMapper.getTotalSalesByDate(today));
        stats.put("orderCount", saleMapper.getOrderCountByDate(today));
        stats.put("customerCount", saleMapper.getUniqueCustomerCountByDate(today));
        stats.put("date", today.toString());

        return stats;
    }

    @Override
    public List<Map<String, Object>> getSalesTrend(String period) {
        List<Map<String, Object>> trendData = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        switch (period) {
            case "week":
                startDate = endDate.minusDays(6);
                break;
            case "month":
                startDate = endDate.minusDays(29);
                break;
            case "year":
                startDate = endDate.minusDays(364);
                break;
            default:
                startDate = endDate.minusDays(6);
        }

        // 生成趋势数据
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("sales", saleMapper.getTotalSalesByDate(date));
            dayData.put("orders", saleMapper.getOrderCountByDate(date));
            trendData.add(dayData);
        }

        return trendData;
    }

    @Override
    public List<Map<String, Object>> getCashierRanking(String startDate, String endDate) {
        // 实现收银员销售排行逻辑
        return saleMapper.getCashierPerformanceToday();
    }

    @Override
    public List<Map<String, Object>> getProductRanking(String startDate, String endDate, int limit) {
        // 实现商品销售排行逻辑
        List<Map<String, Object>> ranking = new ArrayList<>();
        // 这里可以添加具体的排行查询逻辑
        return ranking;
    }

    @Override
    public Map<String, Object> getSalesReport(String startDate, String endDate, String groupBy) {
        Map<String, Object> report = new HashMap<>();

        // 根据groupBy参数生成不同粒度的报表
        switch (groupBy) {
            case "day":
                report.put("type", "daily");
                break;
            case "week":
                report.put("type", "weekly");
                break;
            case "month":
                report.put("type", "monthly");
                break;
            default:
                report.put("type", "daily");
        }

        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("data", new ArrayList<>());

        return report;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNumber() {
        return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%03d", new Random().nextInt(1000));
    }
}
