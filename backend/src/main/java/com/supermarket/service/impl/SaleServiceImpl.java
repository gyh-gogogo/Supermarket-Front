package com.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.entity.Member;
import com.supermarket.entity.Sale;
import com.supermarket.entity.SaleItem;
import com.supermarket.mapper.MemberMapper;
import com.supermarket.mapper.SaleMapper;
import com.supermarket.service.MemberService;
import com.supermarket.service.SaleService;
import com.supermarket.service.SaleItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class SaleServiceImpl extends ServiceImpl<SaleMapper, Sale> implements SaleService {

    @Autowired
    private SaleItemService saleItemService;
    @Autowired
    private MemberService memberService;

    @Override
    public List<Sale> getSales(String startDate, String endDate, String orderNumber, Long cashierId) {
        return List.of();
    }

    @Override
    public Sale getSaleById(Long saleId) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getSaleItems(Long saleId) {
        return List.of();
    }

    @Override
    public Sale createSale(Map<String, Object> saleData) {
        return null;
    }

    @Override
    public boolean cancelSale(Long saleId) {
        return false;
    }

    @Override
    public boolean refundSale(Long saleId, Map<String, Object> refundData) {
        return false;
    }

    @Override
    public Map<String, Object> getTodayStats() {
        return Map.of();
    }

    @Override
    public List<Map<String, Object>> getSalesTrend(String period) {
        return List.of();
    }

    @Override
    public List<Map<String, Object>> getCashierRanking(String startDate, String endDate) {
        return List.of();
    }

    @Override
    public List<Map<String, Object>> getProductRanking(String startDate, String endDate, int limit) {
        return List.of();
    }

    @Override
    public Map<String, Object> getSalesReport(String startDate, String endDate, String groupBy) {
        return Map.of();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> processCheckout(Map<String, Object> checkoutData) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            System.out.println("🛒 开始处理收银台结算...");
            System.out.println("📋 结算数据: " + checkoutData);
            
            // 1. 生成订单号
            String saleNumber = generateSaleNumber();
            System.out.println("📄 生成订单号: " + saleNumber);
            
            // 2. 创建销售订单
            Sale sale = createSaleFromCheckoutData(checkoutData, saleNumber);
            boolean saveSuccess = this.save(sale);
            
            if (!saveSuccess) {
                throw new RuntimeException("保存销售订单失败");
            }
            
            System.out.println("✅ 销售订单保存成功，ID: " + sale.getSaleId());
            
            // 3. 保存销售明细
            List<Map<String, Object>> items = (List<Map<String, Object>>) checkoutData.get("items");
            if (items != null && !items.isEmpty()) {
                saveSaleItems(sale.getSaleId(), items);
                System.out.println("✅ 销售明细保存成功，共 " + items.size() + " 项");
            }
            
            // 4. 更新会员积分（如果是会员支付）
            if (sale.getMemberId() != null) {
                updateMemberPoints(sale.getMemberId(), sale.getFinalAmount());
                System.out.println("✅ 会员积分更新成功");
            }
            
            // 5. 返回成功结果
            result.put("success", true);
            result.put("message", "结算成功");
            result.put("saleId", sale.getSaleId());
            result.put("saleNumber", saleNumber);
            result.put("finalAmount", sale.getFinalAmount());
            
            System.out.println("🎉 收银台结算处理完成");
            
        } catch (Exception e) {
            System.err.println("❌ 收银台结算失败: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "结算失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 生成订单号
     */
    private String generateSaleNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%03d", new Random().nextInt(1000));
        return "S" + timestamp + random;
    }
    
    /**
     * 从结算数据创建销售订单
     */
    private Sale createSaleFromCheckoutData(Map<String, Object> checkoutData, String saleNumber) {
        Sale sale = new Sale();
        sale.setSaleNumber(saleNumber);
        
        // 基本金额信息
        sale.setTotalAmount(getDoubleValue(checkoutData, "totalAmount"));
        sale.setDiscountAmount(getDoubleValue(checkoutData, "discountAmount")); // 统一使用discountAmount
        sale.setFinalAmount(getDoubleValue(checkoutData, "finalAmount"));
        // 支付信息
        sale.setPaymentMethod((String) checkoutData.get("paymentMethod"));
        
        // 关联信息
        sale.setCashierId(getLongValue(checkoutData, "cashierId"));
        sale.setMemberId(getLongValue(checkoutData, "memberId"));
        

        return sale;
    }
    
    /**
     * 保存销售明细
     */
    private void saveSaleItems(Long saleId, List<Map<String, Object>> items) {
        List<SaleItem> saleItems = new ArrayList<>();
        
        for (Map<String, Object> item : items) {
            SaleItem saleItem = new SaleItem(
                saleId,
                getLongValue(item, "productId"),
                (String) item.get("productName"),
                (String) item.get("barcode"),
                getDoubleValue(item, "price"),
                getIntegerValue(item, "quantity"),
                getDoubleValue(item, "subtotal")
            );
            saleItems.add(saleItem);
        }
        
        boolean batchSuccess = saleItemService.saveBatch(saleItems);
        if (!batchSuccess) {
            throw new RuntimeException("保存销售明细失败");
        }
    }
    
    /**
     * 更新会员积分
     */
    private void updateMemberPoints(Long memberId, Double finalAmount) {
        try {
            Member member = memberService.getById(memberId);
            if (member != null) {
                // 消费积分规则：每消费1元获得1积分
                int earnedPoints = finalAmount.intValue();
                
                // 更新积分和累计消费
                member.setPoints((member.getPoints() != null ? member.getPoints() : 0) + earnedPoints);
                member.setTotalConsumption((member.getTotalConsumption() != null ? member.getTotalConsumption() : 0.0) + finalAmount);
                member.setUpdatedAt(LocalDateTime.now());
                
                memberService.updateById(member);
                
                System.out.println("💰 会员积分更新: 会员ID=" + memberId + 
                    ", 获得积分=" + earnedPoints + 
                    ", 当前积分=" + member.getPoints() + 
                    ", 累计消费=" + member.getTotalConsumption());
            }
        } catch (Exception e) {
            System.err.println("❌ 更新会员积分失败: " + e.getMessage());
            // 不抛出异常，避免影响主流程
        }
    }
    
    // 辅助方法
    private Double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return 0.0;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private Integer getIntegerValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return 0;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

