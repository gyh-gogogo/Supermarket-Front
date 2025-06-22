package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.entity.Sale;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 销售数据访问层
 */
@Mapper
public interface SaleMapper extends BaseMapper<Sale> {

    /**
     * 按日期获取销售总额
     */
    BigDecimal getTotalSalesByDate(LocalDate date);

    /**
     * 按日期获取订单数量
     */
    int getOrderCountByDate(LocalDate date);

    /**
     * 按日期获取唯一客户数
     */
    int getUniqueCustomerCountByDate(LocalDate date);

    /**
     * 按月份获取销售总额
     */
    @Select("SELECT COALESCE(SUM(final_amount), 0) FROM sales WHERE YEAR(sale_time) = #{year} AND MONTH(sale_time) = #{month}")
    BigDecimal getTotalSalesByMonth(int year, int month);

    /**
     * 按月份获取订单数量
     */
    @Select("SELECT COUNT(*) FROM sales WHERE YEAR(sale_time) = #{year} AND MONTH(sale_time) = #{month}")
    int getOrderCountByMonth(int year, int month);

    /**
     * 获取最近销售活动（用于Dashboard展示）
     */
    List<Map<String, Object>> getRecentSalesActivities(int limit);

    /**
     * 获取收银员今日业绩
     */
    List<Map<String, Object>> getCashierPerformanceToday();

    /**
     * 获取今日小时销售数据
     */
    List<Map<String, Object>> getHourlySalesToday();

    /**
     * 获取收银员销售历史
     */
    List<Map<String, Object>> getSaleHistoryByCashier(Long cashierId, int limit);

    /**
     * 获取收银员今日销售额
     */
    BigDecimal getTodaySalesByCashier(Long cashierId);

    /**
     * 获取收银员今日订单数
     */
    int getTodayOrdersByCashier(Long cashierId);
}
