package com.supermarket.controller;

import com.supermarket.entity.Product;
import com.supermarket.entity.Member;
import com.supermarket.service.CashierService;
import com.supermarket.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**66
 * 收银台控制器
 */
@RestController
@RequestMapping("/api/cashier")
@CrossOrigin(origins = "*")
public class CashierController {

    @Autowired
    private CashierService cashierService;

    /**
     * 根据条码获取商品信息
     */
    @GetMapping("/product/{barcode}")
    public Result<Product> getProductByBarcode(@PathVariable String barcode) {
        try {
            Product product = cashierService.getProductByBarcode(barcode);
            if (product != null) {
                return Result.success(product);
            } else {
                return Result.error("商品不存在");
            }
        } catch (Exception e) {
            return Result.error("获取商品信息失败: " + e.getMessage());
        }
    }

    /**
     * 根据手机号获取会员信息
     */
    @GetMapping("/member/{phone}")
    public Result<Member> getMemberByPhone(@PathVariable String phone) {
        try {
            Member member = cashierService.getMemberByPhone(phone);
            if (member != null) {
                return Result.success(member);
            } else {
                return Result.error("会员不存在");
            }
        } catch (Exception e) {
            return Result.error("获取会员信息失败: " + e.getMessage());
        }
    }

    /**
     * 处理结算
     */
    @PostMapping("/checkout")
    public Result<Map<String, Object>> processCheckout(@RequestBody Map<String, Object> checkoutData) {
        try {
            Map<String, Object> result = cashierService.processCheckout(checkoutData);
            if ((Boolean) result.get("success")) {
                return Result.success(result);
            } else {
                return Result.error(result.get("message").toString());
            }
        } catch (Exception e) {
            return Result.error("结算失败: " + e.getMessage());
        }
    }

    /**
     * 获取销售历史记录
     */
    @GetMapping("/sales/history")
    public Result<List<Map<String, Object>>> getSaleHistory(
            @RequestParam Long cashierId,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<Map<String, Object>> history = cashierService.getSaleHistory(cashierId, limit);
            return Result.success(history);
        } catch (Exception e) {
            return Result.error("获取销售历史失败: " + e.getMessage());
        }
    }

    /**
     * 获取收银员统计数据
     */
    @GetMapping("/statistics/{cashierId}")
    public Result<Map<String, Object>> getCashierStatistics(@PathVariable Long cashierId) {
        try {
            Map<String, Object> stats = cashierService.getCashierStatistics(cashierId);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 验证商品库存
     */
    @GetMapping("/validate-stock")
    public Result<Boolean> validateProductStock(@RequestParam Long productId, @RequestParam Integer quantity) {
        try {
            boolean hasStock = cashierService.validateProductStock(productId, quantity);
            return Result.success(hasStock);
        } catch (Exception e) {
            return Result.error("验证库存失败: " + e.getMessage());
        }
    }

    /**
     * 获取快捷商品列表
     */
    @GetMapping("/quick-products")
    public Result<List<Product>> getQuickProducts() {
        try {
            List<Product> products = cashierService.getQuickProducts();
            return Result.success(products);
        } catch (Exception e) {
            return Result.error("获取快捷商品失败: " + e.getMessage());
        }
    }

    /**
     * 打印小票
     */
    @PostMapping("/print-receipt")
    public Result<Map<String, Object>> printReceipt(@RequestBody Map<String, Object> request) {
        try {
            Long saleId = Long.valueOf(request.get("saleId").toString());
            Map<String, Object> receipt = cashierService.printReceipt(saleId);
            
            if ((Boolean) receipt.get("success")) {
                return Result.success(receipt);
            } else {
                return Result.error(receipt.get("message").toString());
            }
        } catch (Exception e) {
            return Result.error("生成小票失败: " + e.getMessage());
        }
    }
}