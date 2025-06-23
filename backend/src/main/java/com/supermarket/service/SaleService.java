package com.supermarket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.entity.Sale;
import com.supermarket.entity.SaleItem;

import java.util.List;
import java.util.Map;

/**
 * 销售记录服务接口
 */
public interface SaleService extends IService<Sale> {

    /**
     * 获取销售记录列表
     */
    List<Sale> getSales(String startDate, String endDate, String orderNumber, Long cashierId);

    /**
     * 根据ID获取销售记录
     */
    Sale getSaleById(Long saleId);

    /**
     * 获取销售明细
     */
    List<Map<String, Object>> getSaleItems(Long saleId);

    /**
     * 创建销售记录
     */
    Sale createSale(Map<String, Object> saleData);

    /**
     * 取消销售记录
     */
    boolean cancelSale(Long saleId);

    /**
     * 退货处理
     */
    boolean refundSale(Long saleId, Map<String, Object> refundData);

    /**
     * 获取今日销售统计
     */
    Map<String, Object> getTodayStats();

    /**
     * 获取销售趋势数据
     */
    List<Map<String, Object>> getSalesTrend(String period);

    /**
     * 获取收银员销售排行
     */
    List<Map<String, Object>> getCashierRanking(String startDate, String endDate);

    /**
     * 获取商品销售排行
     */
    List<Map<String, Object>> getProductRanking(String startDate, String endDate, int limit);

    /**
     * 获取销售报表数据
     */
    Map<String, Object> getSalesReport(String startDate, String endDate, String groupBy);

    /**
     * 处理收银台结算
     * @param checkoutData 结算数据
     * @return 处理结果
     */
    Map<String, Object> processCheckout(Map<String, Object> checkoutData);
}
