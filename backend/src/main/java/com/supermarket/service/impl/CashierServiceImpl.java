package com.supermarket.service.impl;

import com.supermarket.entity.Sale;
import com.supermarket.entity.SaleItem;
import com.supermarket.entity.Product;
import com.supermarket.entity.Member;
import com.supermarket.mapper.SaleMapper;
import com.supermarket.mapper.SaleItemMapper;
import com.supermarket.mapper.ProductMapper;
import com.supermarket.mapper.MemberMapper;
import com.supermarket.service.CashierService;
import com.supermarket.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@Transactional
public class CashierServiceImpl implements CashierService {

    @Autowired
    private SaleMapper saleMapper;
    
    @Autowired
    private SaleItemMapper saleItemMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private MemberMapper memberMapper;
    
    @Autowired
    private MemberService memberService;

    @Override
    public Product getProductByBarcode(String barcode) {
        return productMapper.selectByBarcode(barcode);
    }


    @Override
    public Member getMemberByPhone(String phoneNumber) {
        return memberMapper.selectByPhone(phoneNumber);
    }

    @Override
    @Transactional
    public Map<String, Object> processCheckout(Map<String, Object> checkoutData) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 解析结算数据
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) checkoutData.get("items");
            double totalAmountDouble = new BigDecimal(checkoutData.get("totalAmount").toString()).doubleValue();
            double discountAmountDouble = new BigDecimal(checkoutData.get("discountAmount").toString()).doubleValue();
            double finalAmountDouble = new BigDecimal(checkoutData.get("finalAmount").toString()).doubleValue();

            String paymentMethod = checkoutData.get("paymentMethod").toString();
            Long memberId = checkoutData.get("memberId") != null ? 
                Long.valueOf(checkoutData.get("memberId").toString()) : null;
            Long cashierId = Long.valueOf(checkoutData.get("cashierId").toString());

            // 创建销售记录
            Sale sale = new Sale();
            sale.setTotalAmount(totalAmountDouble);
            sale.setDiscountAmount(discountAmountDouble);
            sale.setFinalAmount(finalAmountDouble);
            sale.setPaymentMethod(paymentMethod);
            sale.setCashierId(cashierId);
            sale.setMemberId(memberId);
            sale.setSaleDate(LocalDateTime.now());
            sale.setStatus("completed");
            // 生成订单号
            String orderNumber = generateOrderNumber();
            sale.setSaleNumber(orderNumber);

            // 保存销售记录
            int saleResult = saleMapper.insert(sale);
            if (saleResult <= 0) {
                throw new RuntimeException("保存销售记录失败");
            }

            Long saleId = sale.getSaleId();

            // 保存销售商品明细并更新库存
            for (Map<String, Object> item : items) {
                Long productId = Long.valueOf(item.get("productId").toString());
                Integer quantity = Integer.valueOf(item.get("quantity").toString());
                BigDecimal price = new BigDecimal(item.get("price").toString());
                double priceDouble = price.doubleValue();
                BigDecimal subtotal = new BigDecimal(item.get("subtotal").toString());
                double subtotalDouble=subtotal.doubleValue();

                // 检查库存
                Product product = productMapper.selectById(productId);
                if (product == null) {
                    throw new RuntimeException("商品不存在: " + productId);
                }
                
                if (product.getStockQuantity() < quantity) {
                    throw new RuntimeException("商品库存不足: " + product.getProductName());
                }

                // 创建销售明细
                SaleItem saleItem = new SaleItem();
                saleItem.setSaleId(saleId);
                saleItem.setProductId(productId);
                saleItem.setQuantity(quantity);
                saleItem.setUnitPrice(priceDouble);
                saleItem.setSubtotal(subtotalDouble);

                saleItemMapper.insert(saleItem);

                // 更新商品库存
                product.setStockQuantity(product.getStockQuantity() - quantity);
                productMapper.update(product);
            }

            // 返回结算结果
            result.put("success", true);
            result.put("orderNumber", orderNumber);
            result.put("saleId", saleId);
            result.put("message", "结算成功");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "结算失败: " + e.getMessage());
            throw new RuntimeException("结算失败", e);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getSaleHistory(Long cashierId, int limit) {
        return saleMapper.getSaleHistoryByCashier(cashierId, limit);
    }

    @Override
    public Map<String, Object> getCashierStatistics(Long cashierId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 今日销售额
        BigDecimal todaySales = saleMapper.getTodaySalesByCashier(cashierId);
        stats.put("todaySales", todaySales != null ? todaySales : BigDecimal.ZERO);
        
        // 今日订单数
        int todayOrders = saleMapper.getTodayOrdersByCashier(cashierId);
        stats.put("todayOrders", todayOrders);        // 平均客单价
        BigDecimal avgOrderAmount = todayOrders > 0 ? 
            ((BigDecimal) stats.get("todaySales")).divide(new BigDecimal(todayOrders), 2, java.math.RoundingMode.HALF_UP) 
            : BigDecimal.ZERO;
        stats.put("avgOrderAmount", avgOrderAmount);
        
        return stats;
    }

    @Override
    public boolean validateProductStock(Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        return product != null && product.getStockQuantity() >= quantity;
    }

    @Override
    public BigDecimal calculateMemberDiscount(Long memberId, BigDecimal totalAmount) {
        Member member = memberMapper.selectById(memberId);
        if (member == null) {
            return BigDecimal.ZERO;
        }
        
        // 根据会员等级计算折扣
        BigDecimal discountRate = BigDecimal.ZERO;
        switch (member.getMemberLevel()) {
            case "bronze":
                discountRate = new BigDecimal("0.02"); // 2%折扣
                break;
            case "silver":
                discountRate = new BigDecimal("0.05"); // 5%折扣
                break;
            case "gold":
                discountRate = new BigDecimal("0.08"); // 8%折扣
                break;
            case "diamond":
                discountRate = new BigDecimal("0.12"); // 12%折扣
                break;
        }
        
        return totalAmount.multiply(discountRate);
    }

    @Override
    public List<Product> getQuickProducts() {
        return productMapper.getQuickProducts();
    }

    @Override
    public Map<String, Object> printReceipt(Long saleId) {
        Map<String, Object> receipt = new HashMap<>();
        
        // 获取销售记录
        Sale sale = saleMapper.selectById(saleId);
        if (sale == null) {
            receipt.put("success", false);
            receipt.put("message", "销售记录不存在");
            return receipt;
        }
        
        // 获取销售明细
        List<Map<String, Object>> saleItems = saleItemMapper.getSaleItemsBySaleId(saleId);
        
        // 构建小票数据
        receipt.put("success", true);
        receipt.put("orderNumber", sale.getSaleNumber());
        receipt.put("saleTime", sale.getSaleDate());
        receipt.put("totalAmount", sale.getTotalAmount());
        receipt.put("discountAmount", sale.getDiscountAmount());
        receipt.put("finalAmount", sale.getFinalAmount());
        receipt.put("paymentMethod", sale.getPaymentMethod());
        receipt.put("items", saleItems);
        
        // 会员信息
        if (sale.getMemberId() != null) {
            Member member = memberMapper.selectById(sale.getMemberId());
            if (member != null) {
                Map<String, Object> memberInfo = new HashMap<>();
                memberInfo.put("memberName", member.getMemberName());
                memberInfo.put("phoneNumber", member.getPhone());
                memberInfo.put("memberLevel", member.getMemberLevel());
                memberInfo.put("points", member.getPoints());
                receipt.put("memberInfo", memberInfo);
            }
        }
        
        return receipt;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis();
    }
}