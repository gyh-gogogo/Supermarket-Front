package com.supermarket.service;

import com.supermarket.entity.Product;
import com.supermarket.entity.Member;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 收银台服务接口
 */
public interface CashierService {

    /**
     * 根据条码获取商品信息
     * @param barcode 商品条码
     * @return 商品信息
     */
    Product getProductByBarcode(String barcode);


    /**
     * 根据手机号获取会员信息
     * @param phoneNumber 手机号
     * @return 会员信息
     */
    Member getMemberByPhone(String phoneNumber);

    /**
     * 处理结算
     * @param checkoutData 结算数据
     * @return 结算结果
     */
    Map<String, Object> processCheckout(Map<String, Object> checkoutData);

    /**
     * 获取销售历史记录
     * @param cashierId 收银员ID
     * @param limit 返回数量限制
     * @return 销售历史列表
     */
    List<Map<String, Object>> getSaleHistory(Long cashierId, int limit);

    /**
     * 获取收银员统计数据
     * @param cashierId 收银员ID
     * @return 统计数据
     */
    Map<String, Object> getCashierStatistics(Long cashierId);

    /**
     * 验证商品库存
     * @param productId 商品ID
     * @param quantity 需要数量
     * @return 是否有足够库存
     */
    boolean validateProductStock(Long productId, Integer quantity);

    /**
     * 计算会员折扣
     * @param memberId 会员ID
     * @param totalAmount 总金额
     * @return 折扣金额
     */
    BigDecimal calculateMemberDiscount(Long memberId, BigDecimal totalAmount);

    /**
     * 获取快捷商品列表
     * @return 快捷商品列表
     */
    List<Product> getQuickProducts();

    /**
     * 打印小票
     * @param saleId 销售记录ID
     * @return 小票数据
     */
    Map<String, Object> printReceipt(Long saleId);
}